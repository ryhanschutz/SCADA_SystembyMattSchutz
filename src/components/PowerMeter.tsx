import { Card } from "@/components/ui/card";
import { useEffect, useState } from "react";
import { useScada } from "@/contexts/ScadaContext";
import AnalogMeter from "./AnalogMeter";

const PowerMeter = () => {
  const { current, voltage, powerFactor, isEmergencyActive } = useScada();
  const [thd, setThd] = useState(3.2);

  useEffect(() => {
    const interval = setInterval(() => {
      if (!isEmergencyActive) {
        setThd(2.5 + Math.random() * 2);
      } else {
        setThd(0);
      }
    }, 3000);
    return () => clearInterval(interval);
  }, [isEmergencyActive]);

  const activePower = current * voltage * powerFactor / 1000;
  const reactivePower = current * voltage * Math.sqrt(1 - powerFactor * powerFactor) / 1000;
  const totalPower = Math.sqrt(activePower * activePower + reactivePower * reactivePower);

  return (
    <Card className="p-6 shadow-[var(--shadow-card)] border border-border">
      <h3 className="text-lg font-semibold mb-6 text-foreground">Medidores Principais</h3>
      
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        <div className="flex flex-col">
          <div className="bg-gradient-to-br from-background to-muted/20 border border-border rounded-lg p-4 h-full flex items-center justify-center min-h-[240px] shadow-[var(--shadow-sm)] hover:shadow-[var(--shadow-md)] transition-shadow">
            <AnalogMeter
              value={current}
              maxValue={250}
              label="Corrente"
              unit="A"
            />
          </div>
        </div>
        
        <div className="flex flex-col">
          <div className="bg-gradient-to-br from-background to-muted/20 border border-border rounded-lg p-4 h-full flex items-center justify-center min-h-[240px] shadow-[var(--shadow-sm)] hover:shadow-[var(--shadow-md)] transition-shadow">
            <AnalogMeter
              value={60}
              maxValue={65}
              label="Frequência"
              unit="Hz"
            />
          </div>
        </div>

        <div className="flex flex-col">
          <div className="bg-gradient-to-br from-background to-muted/20 border border-border rounded-lg p-4 h-full flex items-center justify-center min-h-[240px] shadow-[var(--shadow-sm)] hover:shadow-[var(--shadow-md)] transition-shadow">
            <AnalogMeter
              value={totalPower}
              maxValue={3000}
              label="Potência Total"
              unit="kW"
            />
          </div>
        </div>

        <div className="flex flex-col">
          <div className="bg-gradient-to-br from-background to-muted/20 border border-border rounded-lg p-4 h-full flex items-center justify-center min-h-[240px] shadow-[var(--shadow-sm)] hover:shadow-[var(--shadow-md)] transition-shadow">
            <AnalogMeter
              value={reactivePower}
              maxValue={1500}
              label="Reativa"
              unit="kVAr"
            />
          </div>
        </div>

        <div className="flex flex-col">
          <div className="bg-gradient-to-br from-background to-muted/20 border border-border rounded-lg p-4 h-full flex items-center justify-center min-h-[240px] shadow-[var(--shadow-sm)] hover:shadow-[var(--shadow-md)] transition-shadow">
            <AnalogMeter
              value={activePower}
              maxValue={2500}
              label="Ativa"
              unit="kWh"
            />
          </div>
        </div>

        <div className="flex flex-col">
          <div className="bg-gradient-to-br from-background to-muted/20 border border-border rounded-lg p-4 h-full flex items-center justify-center min-h-[240px] shadow-[var(--shadow-sm)] hover:shadow-[var(--shadow-md)] transition-shadow">
            <AnalogMeter
              value={powerFactor}
              maxValue={1}
              label="Fator Potência"
              unit="cos φ"
            />
          </div>
        </div>
      </div>
    </Card>
  );
};

export default PowerMeter;
