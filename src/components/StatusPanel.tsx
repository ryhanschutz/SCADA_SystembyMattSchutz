import { Card } from "@/components/ui/card";
import { Wifi, AlertCircle, CheckCircle, Clock } from "lucide-react";
import { useEffect, useState } from "react";

const StatusPanel = () => {
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const alerts = [
    { type: "success", message: "Sistema operando normalmente", time: "2 min atrás" },
    { type: "warning", message: "Temperatura do motor elevada", time: "5 min atrás" },
    { type: "info", message: "Manutenção agendada", time: "1 hora atrás" },
  ];

  return (
    <div className="space-y-4">
      <Card className="p-6 shadow-[var(--shadow-card)] border border-border hover:shadow-[var(--shadow-md)] transition-shadow">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-foreground">Status da Rede</h3>
          <div className="p-2 bg-success/10 rounded-lg">
            <Wifi className="h-5 w-5 text-success" />
          </div>
        </div>
        <div className="space-y-3 text-sm">
          <div className="flex justify-between items-center p-2 bg-muted/50 rounded">
            <span className="text-muted-foreground">Conexão</span>
            <span className="text-success font-medium">Online</span>
          </div>
          <div className="flex justify-between items-center p-2 bg-muted/50 rounded">
            <span className="text-muted-foreground">Latência</span>
            <span className="text-foreground font-medium">12ms</span>
          </div>
          <div className="flex justify-between items-center p-2 bg-muted/50 rounded">
            <span className="text-muted-foreground">Dispositivos</span>
            <span className="text-foreground font-medium">8/8</span>
          </div>
        </div>
      </Card>

      <Card className="p-6 shadow-[var(--shadow-card)] border border-border hover:shadow-[var(--shadow-md)] transition-shadow">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-foreground">Horário do Sistema</h3>
          <div className="p-2 bg-primary/10 rounded-lg">
            <Clock className="h-5 w-5 text-primary" />
          </div>
        </div>
        <div className="text-center bg-muted/50 rounded-lg p-4">
          <p className="text-3xl font-bold text-foreground tabular-nums">
            {currentTime.toLocaleTimeString()}
          </p>
          <p className="text-sm text-muted-foreground mt-2">
            {currentTime.toLocaleDateString()}
          </p>
        </div>
      </Card>

      <Card className="p-6 shadow-[var(--shadow-card)] border border-border hover:shadow-[var(--shadow-md)] transition-shadow">
        <h3 className="font-semibold mb-4 text-foreground">Alertas Recentes</h3>
        <div className="space-y-3">
          {alerts.map((alert, index) => (
            <div key={index} className="flex items-start gap-3 p-3 bg-muted/30 rounded-lg hover:bg-muted/50 transition-colors">
              <div className="shrink-0">
                {alert.type === "success" && (
                  <div className="p-1.5 bg-success/10 rounded">
                    <CheckCircle className="h-4 w-4 text-success" />
                  </div>
                )}
                {alert.type === "warning" && (
                  <div className="p-1.5 bg-warning/10 rounded">
                    <AlertCircle className="h-4 w-4 text-warning" />
                  </div>
                )}
                {alert.type === "info" && (
                  <div className="p-1.5 bg-primary/10 rounded">
                    <AlertCircle className="h-4 w-4 text-primary" />
                  </div>
                )}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-foreground font-medium">{alert.message}</p>
                <p className="text-xs text-muted-foreground mt-1">{alert.time}</p>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
};

export default StatusPanel;
