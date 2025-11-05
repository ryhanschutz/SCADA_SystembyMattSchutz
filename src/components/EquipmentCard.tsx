import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Play, Square, Activity } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";

interface EquipmentCardProps {
  name: string;
  icon: React.ReactNode;
  status: "running" | "stopped" | "warning";
}

const EquipmentCard = ({ name, icon, status: initialStatus }: EquipmentCardProps) => {
  const [status, setStatus] = useState(initialStatus);
  const [isRunning, setIsRunning] = useState(initialStatus === "running");

  const handleStart = () => {
    setIsRunning(true);
    setStatus("running");
    toast.success(`${name} started successfully`);
  };

  const handleStop = () => {
    setIsRunning(false);
    setStatus("stopped");
    toast.info(`${name} stopped`);
  };

  const statusColors = {
    running: "text-success",
    stopped: "text-muted-foreground",
    warning: "text-warning",
  };

  const statusBgColors = {
    running: "bg-success/10",
    stopped: "bg-muted",
    warning: "bg-warning/10",
  };

  return (
    <Card className={`p-6 shadow-[var(--shadow-card)] ${statusBgColors[status]} border-2 transition-all duration-300`}>
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-3">
          <div className={`p-2 rounded-lg bg-card ${statusColors[status]}`}>
            {icon}
          </div>
          <h3 className="font-semibold text-foreground">{name}</h3>
        </div>
        <div className="flex items-center gap-2">
          <Activity className={`h-4 w-4 ${statusColors[status]} ${isRunning ? 'animate-pulse' : ''}`} />
          <span className={`text-sm font-medium ${statusColors[status]} capitalize`}>
            {status}
          </span>
        </div>
      </div>
      <div className="flex gap-2">
        <Button
          onClick={handleStart}
          disabled={isRunning}
          size="sm"
          className="flex-1 bg-success hover:bg-success/90 text-success-foreground"
        >
          <Play className="h-4 w-4 mr-1" />
          Start
        </Button>
        <Button
          onClick={handleStop}
          disabled={!isRunning}
          size="sm"
          variant="secondary"
          className="flex-1"
        >
          <Square className="h-4 w-4 mr-1" />
          Stop
        </Button>
      </div>
    </Card>
  );
};

export default EquipmentCard;
