import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from "recharts";
import { Card } from "@/components/ui/card";

interface TrendChartProps {
  data: Array<{
    timestamp: number;
    current: number;
    voltage: number;
    power: number;
    temperature: number;
  }>;
  title: string;
}

const TrendChart = ({ data, title }: TrendChartProps) => {
  const chartData = data.map(d => ({
    time: new Date(d.timestamp).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
    corrente: d.current,
    potencia: d.power,
    temperatura: d.temperature
  }));

  return (
    <Card className="p-2 shadow-[var(--shadow-card)]">
      <h4 className="text-xs font-semibold mb-2 text-foreground">{title}</h4>
      <ResponsiveContainer width="100%" height={140}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" opacity={0.3} />
          <XAxis 
            dataKey="time" 
            stroke="hsl(var(--muted-foreground))"
            tick={{ fontSize: 8 }}
          />
          <YAxis 
            stroke="hsl(var(--muted-foreground))"
            tick={{ fontSize: 8 }}
          />
          <Tooltip 
            contentStyle={{ 
              backgroundColor: "hsl(var(--card))", 
              border: "1px solid hsl(var(--border))",
              fontSize: 10
            }} 
          />
          <Legend wrapperStyle={{ fontSize: 9 }} />
          <Line type="monotone" dataKey="corrente" stroke="hsl(var(--chart-1))" strokeWidth={1.5} dot={false} name="I(A)" />
          <Line type="monotone" dataKey="potencia" stroke="hsl(var(--chart-3))" strokeWidth={1.5} dot={false} name="P(kW)" />
          <Line type="monotone" dataKey="temperatura" stroke="hsl(var(--chart-4))" strokeWidth={1.5} dot={false} name="T(°C)" />
        </LineChart>
      </ResponsiveContainer>
    </Card>
  );
};

export default TrendChart;
