import { Card } from "@/components/ui/card";
import { useEffect, useState } from "react";

interface TemperatureGaugeProps {
  label: string;
  color: string;
}

const TemperatureGauge = ({ label, color }: TemperatureGaugeProps) => {
  const [temp, setTemp] = useState(45);

  useEffect(() => {
    const interval = setInterval(() => {
      setTemp(40 + Math.random() * 15);
    }, 4000);
    return () => clearInterval(interval);
  }, []);

  return (
    <Card className="p-4 shadow-[var(--shadow-card)]">
      <h4 className="text-sm font-semibold mb-3 text-foreground">{label}</h4>
      <div className="relative w-full aspect-square max-w-[150px] mx-auto">
        <svg viewBox="0 0 100 100" className="w-full h-auto">
          <circle
            cx="50"
            cy="50"
            r="45"
            fill="none"
            stroke="hsl(var(--muted))"
            strokeWidth="8"
          />
          <circle
            cx="50"
            cy="50"
            r="45"
            fill="none"
            stroke={color}
            strokeWidth="8"
            strokeDasharray={`${(temp / 100) * 282.6} 282.6`}
            strokeLinecap="round"
            transform="rotate(-90 50 50)"
            style={{ transition: "stroke-dasharray 0.5s ease" }}
          />
          <text
            x="50"
            y="50"
            textAnchor="middle"
            dominantBaseline="middle"
            className="text-2xl font-bold fill-foreground"
          >
            {temp.toFixed(0)}°
          </text>
          <text
            x="50"
            y="65"
            textAnchor="middle"
            className="text-xs fill-muted-foreground"
          >
            Celsius
          </text>
        </svg>
      </div>
    </Card>
  );
};

export default TemperatureGauge;
