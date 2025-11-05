import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Play, Square, Activity, ChevronDown, ChevronUp } from "lucide-react";
import { toast } from "sonner";
import { useScada } from "@/contexts/ScadaContext";
import { useAuth } from "@/contexts/AuthContext";
import { useState } from "react";
import EquipmentWaveform from "./EquipmentWaveform";
import TrendChart from "./TrendChart";
import InverterControls from "./InverterControls";

interface EquipmentCardExpandedProps {
  id: string;
  name: string;
  icon: React.ReactNode;
}

const EquipmentCardExpanded = ({ id, name, icon }: EquipmentCardExpandedProps) => {
  const { equipment, updateEquipmentStatus, isEmergencyActive } = useScada();
  const { canControl } = useAuth();
  const [isExpanded, setIsExpanded] = useState(false);
  const eq = equipment.find(e => e.id === id);
  
  if (!eq) return null;

  const handleStart = () => {
    if (!canControl) {
      toast.error("Você não tem permissão para controlar equipamentos");
      return;
    }
    if (isEmergencyActive) {
      toast.error("Não é possível iniciar durante emergência");
      return;
    }
    const ok = updateEquipmentStatus(id, "running");
    if (!ok) {
      toast.warning("Trava de partida ativa para motores. Aguarde alguns segundos.");
      return;
    }
    toast.success(`${name} iniciado com sucesso`);
  };

  const handleStop = () => {
    if (!canControl) {
      toast.error("Você não tem permissão para controlar equipamentos");
      return;
    }
    updateEquipmentStatus(id, "stopped");
    toast.info(`${name} parado`);
  };

  const statusColors = {
    running: "text-success",
    stopped: "text-muted-foreground",
  };

  const statusBgColors = {
    running: "bg-success/10",
    stopped: "bg-muted",
  };

  return (
    <Card className={`p-4 shadow-[var(--shadow-card)] border hover:shadow-[var(--shadow-md)] ${statusBgColors[eq.status]} transition-all duration-300`}>
      {/* Header compacto */}
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2">
          <div className={`p-1.5 rounded-lg bg-card ${statusColors[eq.status]}`}>
            {icon}
          </div>
          <div>
            <h3 className="text-sm font-semibold text-foreground">{name}</h3>
            <p className="text-[10px] text-muted-foreground capitalize">{eq.type}</p>
          </div>
        </div>
        <div className="flex items-center gap-1">
          <Activity className={`h-3 w-3 ${statusColors[eq.status]} ${eq.status === 'running' ? 'animate-pulse' : ''}`} />
          <Button
            onClick={handleStart}
            disabled={eq.status === 'running' || isEmergencyActive || !canControl}
            size="sm"
            className="h-7 px-2 text-xs"
          >
            <Play className="h-3 w-3" />
          </Button>
          <Button
            onClick={handleStop}
            disabled={eq.status === 'stopped' || !canControl}
            size="sm"
            variant="destructive"
            className="h-7 px-2 text-xs"
          >
            <Square className="h-3 w-3" />
          </Button>
          <Button
            onClick={() => setIsExpanded(!isExpanded)}
            size="sm"
            variant="ghost"
            className="h-7 px-2"
          >
            {isExpanded ? <ChevronUp className="h-3 w-3" /> : <ChevronDown className="h-3 w-3" />}
          </Button>
        </div>
      </div>

      {/* Medidores compactos */}
      <div className="grid grid-cols-4 gap-1.5 text-xs mb-2">
        <div className="bg-card/50 p-1.5 rounded border border-border">
          <p className="text-[9px] text-muted-foreground">I</p>
          <p className="text-sm font-bold text-chart-1">{eq.current.toFixed(1)} A</p>
        </div>
        <div className="bg-card/50 p-1.5 rounded border border-border">
          <p className="text-[9px] text-muted-foreground">V</p>
          <p className="text-sm font-bold text-chart-2">{eq.voltage.toFixed(0)} V</p>
        </div>
        <div className="bg-card/50 p-1.5 rounded border border-border">
          <p className="text-[9px] text-muted-foreground">P</p>
          <p className="text-sm font-bold text-chart-3">{eq.power.toFixed(0)} W</p>
        </div>
        <div className="bg-card/50 p-1.5 rounded border border-border">
          <p className="text-[9px] text-muted-foreground">T</p>
          <p className="text-sm font-bold text-chart-4">{eq.temperature.toFixed(0)} °C</p>
        </div>
      </div>

      {/* Área expansível para gráficos */}
      {isExpanded && (
        <div className="space-y-2 animate-accordion-down">
          {eq.type === 'inverter' ? (
            <InverterControls
              equipmentId={id}
              equipmentName={name}
              isRunning={eq.status === 'running'}
              onStartStop={eq.status === 'running' ? handleStop : handleStart}
              disabled={isEmergencyActive || !canControl}
            />
          ) : (
            <EquipmentWaveform 
              current={eq.current} 
              equipmentName={name}
              isRunning={eq.status === 'running'}
            />
          )}
          {eq.history.length > 0 && (
            <TrendChart 
              data={eq.history}
              title="Tendências"
            />
          )}
        </div>
      )}
    </Card>
  );
};

export default EquipmentCardExpanded;
