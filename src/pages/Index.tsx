import { useSearchParams } from "react-router-dom";
import Header from "@/components/Header";
import WaveformChart from "@/components/WaveformChart";
import PowerMeter from "@/components/PowerMeter";
import EquipmentCardExpanded from "@/components/EquipmentCardExpanded";
import EmergencyStop from "@/components/EmergencyStop";
import StatusPanel from "@/components/StatusPanel";
import TrendChart from "@/components/TrendChart";
import { Zap, Settings, Battery, Gauge, Cpu, Lightbulb } from "lucide-react";
import ProtectedRoute from "@/components/ProtectedRoute";
import { ScadaProvider, useScada } from "@/contexts/ScadaContext";

const MonitoringMetrics = () => {
  const { current } = useScada();

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div className="bg-gradient-to-br from-chart-1/10 to-chart-1/5 border-2 border-chart-1/30 rounded-lg p-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-muted-foreground mb-1">Corrente Total</p>
            <p className="text-4xl font-bold text-foreground">{current.toFixed(1)}</p>
            <p className="text-sm text-muted-foreground mt-1">Amperes (A)</p>
          </div>
          <Zap className="h-16 w-16 text-chart-1" />
        </div>
      </div>

      <div className="bg-gradient-to-br from-chart-2/10 to-chart-2/5 border-2 border-chart-2/30 rounded-lg p-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-muted-foreground mb-1">Tensão da Rede</p>
            <p className="text-4xl font-bold text-foreground">380</p>
            <p className="text-sm text-muted-foreground mt-1">Volts (V)</p>
          </div>
          <Battery className="h-16 w-16 text-chart-2" />
        </div>
      </div>

      <div className="bg-gradient-to-br from-chart-3/10 to-chart-3/5 border-2 border-chart-3/30 rounded-lg p-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-muted-foreground mb-1">Entrada do Trafo</p>
            <p className="text-4xl font-bold text-foreground">13.6</p>
            <p className="text-sm text-muted-foreground mt-1">Kilovolts (kV)</p>
          </div>
          <Gauge className="h-16 w-16 text-chart-3" />
        </div>
      </div>
    </div>
  );
};

const HistoricalData = () => {
  const { equipment } = useScada();
  
  const allHistoricalData = equipment.flatMap(eq => 
    eq.history.map(h => ({
      timestamp: h.timestamp,
      equipment: eq.name,
      current: h.current,
      voltage: h.voltage,
      power: h.power,
      temperature: h.temperature
    }))
  ).sort((a, b) => b.timestamp - a.timestamp).slice(0, 50);

  return (
    <div className="space-y-4">
      <TrendChart 
        title="Histórico de Corrente, Potência e Temperatura" 
        data={allHistoricalData} 
      />
      
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {equipment.map(eq => (
          <TrendChart 
            key={eq.id}
            title={`${eq.name} - Histórico`} 
            data={eq.history.slice(-20)} 
          />
        ))}
      </div>
    </div>
  );
};

const EquipmentGrid = () => {
  const { equipment } = useScada();
  
  const getIcon = (type: string) => {
    switch (type) {
      case 'motor': return <Zap className="h-6 w-6" />;
      case 'transformer': return <Settings className="h-6 w-6" />;
      case 'capacitor': return <Battery className="h-6 w-6" />;
      case 'inverter': return <Cpu className="h-6 w-6" />;
      case 'generator': return <Lightbulb className="h-6 w-6" />;
      default: return <Gauge className="h-6 w-6" />;
    }
  };

  return (
    <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
      {equipment.map(eq => (
        <EquipmentCardExpanded
          key={eq.id}
          id={eq.id}
          name={eq.name}
          icon={getIcon(eq.type)}
        />
      ))}
    </div>
  );
};

const Index = () => {
  const [searchParams] = useSearchParams();
  const currentTab = searchParams.get('tab') || 'dashboard';

  return (
    <ProtectedRoute>
      <ScadaProvider>
        <div className="min-h-screen flex flex-col w-full bg-background">
          <Header />
          
          <main className="flex-1 overflow-auto">
            <div className="container mx-auto p-6 max-w-[1600px]">
              {/* Dashboard Tab */}
              {currentTab === 'dashboard' && (
                <div className="space-y-6">
                  <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
                    <div className="lg:col-span-3 space-y-6">
                      <EmergencyStop />
                      <PowerMeter />
                    </div>
                    <div className="lg:col-span-1">
                      <StatusPanel />
                    </div>
                  </div>
                </div>
              )}

              {/* Monitoring Tab */}
              {currentTab === 'monitoring' && (
                <div className="space-y-6">
                  <MonitoringMetrics />
                  <WaveformChart />
                </div>
              )}

              {/* Equipment Tab */}
              {currentTab === 'equipment' && (
                <div className="space-y-6">
                  <EquipmentGrid />
                </div>
              )}

              {/* History Tab */}
              {currentTab === 'history' && (
                <div className="space-y-6">
                  <HistoricalData />
                </div>
              )}
            </div>
          </main>
        </div>
      </ScadaProvider>
    </ProtectedRoute>
  );
};

export default Index;
