import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Slider } from "@/components/ui/slider";
import { Badge } from "@/components/ui/badge";
import { Gauge, Zap, TrendingUp, Settings2 } from "lucide-react";

interface InverterControlsProps {
  equipmentId: string;
  equipmentName: string;
  isRunning: boolean;
  onStartStop: () => void;
  disabled?: boolean;
}

const InverterControls = ({ 
  equipmentName, 
  isRunning,
  onStartStop,
  disabled 
}: InverterControlsProps) => {
  const [frequency, setFrequency] = useState(60);
  const [speed, setSpeed] = useState(1750);
  const [torque, setTorque] = useState(0);
  const [accelRamp, setAccelRamp] = useState(10);
  const [decelRamp, setDecelRamp] = useState(10);
  const [softStart, setSoftStart] = useState(true);

  // Calcular torque baseado em velocidade e frequência
  const calculateTorque = (spd: number, freq: number) => {
    if (!isRunning) return 0;
    const baseTorque = (spd / 1800) * (freq / 60) * 100;
    return Math.min(100, Math.max(0, baseTorque + (Math.random() - 0.5) * 5));
  };

  const handleFrequencyChange = (value: number[]) => {
    const newFreq = value[0];
    setFrequency(newFreq);
    const newSpeed = Math.round((newFreq / 60) * 1800);
    setSpeed(newSpeed);
    setTorque(calculateTorque(newSpeed, newFreq));
  };

  const handleSpeedChange = (value: number[]) => {
    const newSpeed = value[0];
    setSpeed(newSpeed);
    const newFreq = (newSpeed / 1800) * 60;
    setFrequency(Math.round(newFreq * 10) / 10);
    setTorque(calculateTorque(newSpeed, newFreq));
  };

  return (
    <Card className="p-4 border-primary/20 bg-gradient-to-br from-card to-muted/5">
      <div className="space-y-4">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Settings2 className="h-5 w-5 text-primary" />
            <h4 className="font-semibold text-foreground">Controles do Inversor</h4>
          </div>
          <Badge variant={isRunning ? "default" : "secondary"} className="text-xs">
            {isRunning ? "Operando" : "Parado"}
          </Badge>
        </div>

        {/* Medidores Digitais */}
        <div className="grid grid-cols-3 gap-3">
          <div className="bg-muted/30 border border-border p-3 space-y-1">
            <div className="flex items-center gap-1.5">
              <Zap className="h-3.5 w-3.5 text-chart-1" />
              <span className="text-xs text-muted-foreground">Frequência</span>
            </div>
            <div className="text-2xl font-bold text-foreground tabular-nums">
              {frequency.toFixed(1)}
            </div>
            <div className="text-xs text-muted-foreground">Hz</div>
          </div>

          <div className="bg-muted/30 border border-border p-3 space-y-1">
            <div className="flex items-center gap-1.5">
              <TrendingUp className="h-3.5 w-3.5 text-chart-2" />
              <span className="text-xs text-muted-foreground">Velocidade</span>
            </div>
            <div className="text-2xl font-bold text-foreground tabular-nums">
              {speed}
            </div>
            <div className="text-xs text-muted-foreground">RPM</div>
          </div>

          <div className="bg-muted/30 border border-border p-3 space-y-1">
            <div className="flex items-center gap-1.5">
              <Gauge className="h-3.5 w-3.5 text-chart-3" />
              <span className="text-xs text-muted-foreground">Torque</span>
            </div>
            <div className="text-2xl font-bold text-foreground tabular-nums">
              {torque.toFixed(0)}
            </div>
            <div className="text-xs text-muted-foreground">%</div>
          </div>
        </div>

        {/* Controles de Frequência */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <label className="text-sm font-medium text-foreground">Frequência (Hz)</label>
            <span className="text-sm text-muted-foreground tabular-nums">{frequency.toFixed(1)} Hz</span>
          </div>
          <Slider
            value={[frequency]}
            onValueChange={handleFrequencyChange}
            min={0}
            max={120}
            step={0.5}
            disabled={!isRunning || disabled}
            className="w-full"
          />
          <div className="flex justify-between text-xs text-muted-foreground">
            <span>0 Hz</span>
            <span>60 Hz</span>
            <span>120 Hz</span>
          </div>
        </div>

        {/* Controles de Velocidade */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <label className="text-sm font-medium text-foreground">Velocidade (RPM)</label>
            <span className="text-sm text-muted-foreground tabular-nums">{speed} RPM</span>
          </div>
          <Slider
            value={[speed]}
            onValueChange={handleSpeedChange}
            min={0}
            max={3600}
            step={10}
            disabled={!isRunning || disabled}
            className="w-full"
          />
          <div className="flex justify-between text-xs text-muted-foreground">
            <span>0</span>
            <span>1800</span>
            <span>3600</span>
          </div>
        </div>

        {/* Configurações de Rampa */}
        <div className="grid grid-cols-2 gap-3 pt-2 border-t border-border">
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <label className="text-xs font-medium text-foreground">Aceleração</label>
              <span className="text-xs text-muted-foreground tabular-nums">{accelRamp}s</span>
            </div>
            <Slider
              value={[accelRamp]}
              onValueChange={(v) => setAccelRamp(v[0])}
              min={1}
              max={60}
              step={1}
              disabled={disabled}
              className="w-full"
            />
          </div>

          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <label className="text-xs font-medium text-foreground">Desaceleração</label>
              <span className="text-xs text-muted-foreground tabular-nums">{decelRamp}s</span>
            </div>
            <Slider
              value={[decelRamp]}
              onValueChange={(v) => setDecelRamp(v[0])}
              min={1}
              max={60}
              step={1}
              disabled={disabled}
              className="w-full"
            />
          </div>
        </div>

        {/* Soft Start */}
        <div className="flex items-center justify-between pt-2 border-t border-border">
          <label className="text-sm font-medium text-foreground">Soft Start</label>
          <Button
            variant={softStart ? "default" : "outline"}
            size="sm"
            onClick={() => setSoftStart(!softStart)}
            disabled={disabled}
            className="h-8"
          >
            {softStart ? "Ativado" : "Desativado"}
          </Button>
        </div>

        {/* Botão Principal */}
        <Button
          onClick={onStartStop}
          disabled={disabled}
          variant={isRunning ? "destructive" : "default"}
          className="w-full"
          size="lg"
        >
          {isRunning ? "Parar Motor" : "Iniciar Motor"}
        </Button>

        {/* Informações Técnicas */}
        <div className="text-xs text-muted-foreground bg-muted/20 p-2 border-l-2 border-primary/40">
          <p className="font-medium mb-1">Configuração Atual:</p>
          <ul className="space-y-0.5 ml-2">
            <li>• Rampa de aceleração: {accelRamp}s</li>
            <li>• Rampa de desaceleração: {decelRamp}s</li>
            <li>• Soft Start: {softStart ? "Ativo" : "Inativo"}</li>
            <li>• Modo: V/F (Tensão/Frequência)</li>
          </ul>
        </div>
      </div>
    </Card>
  );
};

export default InverterControls;
