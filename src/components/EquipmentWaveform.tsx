import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from "recharts";
import { Card } from "@/components/ui/card";
import { useEffect, useState } from "react";

interface EquipmentWaveformProps {
  current: number;
  equipmentName: string;
  isRunning: boolean;
}

const EquipmentWaveform = ({ current, equipmentName, isRunning }: EquipmentWaveformProps) => {
  const [data, setData] = useState<any[]>([]);
  const [phase, setPhase] = useState(0);

  useEffect(() => {
    const generateData = () => {
      const newData = [];
      const amplitude = isRunning ? current : 0;
      const variation = Math.random() * 0.1 + 0.95;
      
      for (let i = 0; i < 60; i++) {
        const t = i * 0.033;
        newData.push({
          time: t.toFixed(3),
          phaseA: Math.sin(2 * Math.PI * t + phase) * amplitude * variation,
          phaseB: Math.sin(2 * Math.PI * t - (2 * Math.PI) / 3 + phase) * amplitude * variation,
          phaseC: Math.sin(2 * Math.PI * t + (2 * Math.PI) / 3 + phase) * amplitude * variation,
        });
      }
      setData(newData);
      setPhase(prev => (prev + 0.1) % (2 * Math.PI));
    };

    generateData();
    const interval = setInterval(generateData, 100);
    return () => clearInterval(interval);
  }, [phase, isRunning, current]);

  return (
    <Card className="p-4 shadow-[var(--shadow-card)]">
      <h4 className="text-sm font-semibold mb-3 text-foreground">Forma de Onda - {equipmentName}</h4>
      <ResponsiveContainer width="100%" height={180}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
          <XAxis 
            dataKey="time" 
            stroke="hsl(var(--muted-foreground))"
            tick={{ fontSize: 10 }}
          />
          <YAxis 
            stroke="hsl(var(--muted-foreground))"
            tick={{ fontSize: 10 }}
          />
          <Tooltip 
            contentStyle={{ 
              backgroundColor: "hsl(var(--card))", 
              border: "1px solid hsl(var(--border))",
              fontSize: 12
            }} 
          />
          <Legend wrapperStyle={{ fontSize: 11 }} />
          <Line type="monotone" dataKey="phaseA" stroke="hsl(var(--chart-1))" strokeWidth={1.5} dot={false} name="Fase A" />
          <Line type="monotone" dataKey="phaseB" stroke="hsl(var(--chart-2))" strokeWidth={1.5} dot={false} name="Fase B" />
          <Line type="monotone" dataKey="phaseC" stroke="hsl(var(--chart-3))" strokeWidth={1.5} dot={false} name="Fase C" />
        </LineChart>
      </ResponsiveContainer>
    </Card>
  );
};

export default EquipmentWaveform;
