import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { AlertTriangle, Power } from "lucide-react";
import { useScada } from "@/contexts/ScadaContext";
import { useAuth } from "@/contexts/AuthContext";
import { toast } from "sonner";
import emergencyButton from "@/assets/buttonicon.png";

const EmergencyStop = () => {
  const { isEmergencyActive, setEmergencyActive } = useScada();
  const { canControl } = useAuth();

  const handleEmergencyStop = () => {
    if (!canControl) {
      toast.error("Você não tem permissão para acionar parada de emergência");
      return;
    }
    setEmergencyActive(true);
    toast.error("PARADA DE EMERGÊNCIA ATIVADA!");
  };

  const handleReset = () => {
    if (!canControl) {
      toast.error("Você não tem permissão para resetar parada de emergência");
      return;
    }
    setEmergencyActive(false);
    toast.success("Sistema resetado - pronto para operação");
  };

  return (
    <Card className={`p-6 border transition-all ${
      isEmergencyActive 
        ? 'bg-destructive/5 border-destructive shadow-[0_0_20px_rgba(239,68,68,0.3)]' 
        : 'bg-card border-border shadow-[var(--shadow-card)]'
    }`}>
      <div className="flex flex-col md:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className={`p-3 rounded-lg ${
            isEmergencyActive ? 'bg-destructive/10' : 'bg-muted'
          }`}>
            {isEmergencyActive ? (
              <AlertTriangle className="h-8 w-8 text-destructive animate-pulse" />
            ) : (
              <Power className="h-8 w-8 text-muted-foreground" />
            )}
          </div>
          <div>
            <h3 className="text-lg font-semibold text-foreground">Parada de Emergência</h3>
            <p className={`text-sm mt-1 ${
              isEmergencyActive ? 'text-destructive font-medium' : 'text-muted-foreground'
            }`}>
              {isEmergencyActive ? 'SISTEMA EM PARADA DE EMERGÊNCIA' : 'Sistema operando normalmente'}
            </p>
          </div>
        </div>
        
        <div className="flex gap-3 items-center">
          {!isEmergencyActive ? (
            <button
              onClick={handleEmergencyStop}
              disabled={!canControl}
              className="relative group disabled:opacity-50 disabled:cursor-not-allowed transition-transform hover:scale-105 active:scale-95"
            >
              <img 
                src={emergencyButton} 
                alt="Emergency Stop Button" 
                className="w-32 h-32 drop-shadow-lg"
              />
            </button>
          ) : (
            <Button
              onClick={handleReset}
              disabled={!canControl}
              variant="default"
              size="lg"
              className="shadow-md hover:shadow-lg"
            >
              <Power className="mr-2 h-5 w-5" />
              Resetar Sistema
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
};

export default EmergencyStop;
