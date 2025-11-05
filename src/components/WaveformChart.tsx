import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from "recharts";
import { Card } from "@/components/ui/card";
import { useEffect, useState } from "react";
import { useScada } from "@/contexts/ScadaContext";

const WaveformChart = () => {
  const [data, setData] = useState<any[]>([]);
  const [phase, setPhase] = useState(0);
  const { isEmergencyActive, current } = useScada();

  useEffect(() => {
    const generateData = () => {
      const newData = [];
      const amplitude = isEmergencyActive ? 0 : current;
      const variation = Math.random() * 0.1 + 0.95;
      
      for (let i = 0; i < 100; i++) {
        const t = i * 0.02;
        newData.push({
          time: t.toFixed(2),
          phaseA: Math.sin(2 * Math.PI * t + phase) * amplitude * variation,
          phaseB: Math.sin(2 * Math.PI * t - (2 * Math.PI) / 3 + phase) * amplitude * variation,
          phaseC: Math.sin(2 * Math.PI * t + (2 * Math.PI) / 3 + phase) * amplitude * variation,
        });
      }
      setData(newData);
      setPhase(prev => (prev + 0.1) % (2 * Math.PI));
    };

    const interval = setInterval(generateData, 100);
    return () => clearInterval(interval);
  }, [isEmergencyActive, current]);

  return (
    <Card className="p-6 shadow-[var(--shadow-card)]">
      <h3 className="text-lg font-semibold mb-4 text-foreground">Forma de Onda de Corrente</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
          <XAxis dataKey="time" stroke="hsl(var(--muted-foreground))" />
          <YAxis stroke="hsl(var(--muted-foreground))" />
          <Tooltip 
            contentStyle={{ 
              backgroundColor: "hsl(var(--card))", 
              border: "1px solid hsl(var(--border))" 
            }} 
          />
          <Legend />
          <Line type="monotone" dataKey="phaseA" stroke="hsl(var(--chart-1))" strokeWidth={2} dot={false} name="Fase A" />
          <Line type="monotone" dataKey="phaseB" stroke="hsl(var(--chart-2))" strokeWidth={2} dot={false} name="Fase B" />
          <Line type="monotone" dataKey="phaseC" stroke="hsl(var(--chart-3))" strokeWidth={2} dot={false} name="Fase C" />
        </LineChart>
      </ResponsiveContainer>
    </Card>
  );
};

export default WaveformChart;
