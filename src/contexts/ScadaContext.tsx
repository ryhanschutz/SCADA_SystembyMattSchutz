import React, { createContext, useContext, useState, useEffect } from 'react';

interface HistoricalData {
  timestamp: number;
  current: number;
  voltage: number;
  power: number;
  temperature: number;
}

interface InrushLog {
  timestamp: number;
  equipmentId: string;
  equipmentName: string;
  inrushCurrent: number;
  nominalCurrent: number;
  inrushFactor: number;
  alarm: boolean;
}

interface Equipment {
  id: string;
  name: string;
  type: 'motor' | 'transformer' | 'capacitor' | 'inverter' | 'generator';
  status: 'running' | 'stopped';
  current: number;
  voltage: number;
  power: number;
  temperature: number;
  history: HistoricalData[];
  nominalCurrent?: number;
  capacitance?: number; // Para capacitores, em Farads
}

interface ScadaContextType {
  isEmergencyActive: boolean;
  setEmergencyActive: (active: boolean) => void;
  equipment: Equipment[];
  updateEquipmentStatus: (id: string, status: 'running' | 'stopped') => boolean;
  powerFactor: number;
  current: number;
  voltage: number;
  interlockActive: boolean;
  inrushLogs: InrushLog[];
}

const ScadaContext = createContext<ScadaContextType | undefined>(undefined);

export const ScadaProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isEmergencyActive, setIsEmergencyActive] = useState(false);
  const [equipment, setEquipment] = useState<Equipment[]>([
    { id: '1', name: 'Motor A1', type: 'motor', status: 'running', current: 45, voltage: 380, power: 29.6, temperature: 65, history: [], nominalCurrent: 45 },
    { id: '2', name: 'Motor B2', type: 'motor', status: 'stopped', current: 0, voltage: 380, power: 0, temperature: 25, history: [], nominalCurrent: 42 },
    { id: '3', name: 'Motor C3', type: 'motor', status: 'running', current: 38, voltage: 380, power: 25.1, temperature: 58, history: [], nominalCurrent: 38 },
    { id: '4', name: 'Transformador Principal', type: 'transformer', status: 'running', current: 120, voltage: 13800, power: 2872, temperature: 55, history: [], nominalCurrent: 120 },
    { id: '5', name: 'Transformador Auxiliar', type: 'transformer', status: 'stopped', current: 0, voltage: 13800, power: 0, temperature: 30, history: [], nominalCurrent: 95 },
    { id: '6', name: 'Banco de Capacitores', type: 'capacitor', status: 'stopped', current: 0, voltage: 380, power: 0, temperature: 25, history: [], nominalCurrent: 30, capacitance: 0.001 },
    { id: '7', name: 'Motor com Driver VFD-1', type: 'inverter', status: 'running', current: 52, voltage: 380, power: 34, temperature: 48, history: [], nominalCurrent: 52 },
    { id: '8', name: 'Motor com Driver VFD-2', type: 'inverter', status: 'stopped', current: 0, voltage: 380, power: 0, temperature: 28, history: [], nominalCurrent: 48 },
    { id: '9', name: 'Gerador de Emergência', type: 'generator', status: 'stopped', current: 0, voltage: 380, power: 0, temperature: 22, history: [], nominalCurrent: 180 },
  ]);
  const [powerFactor, setPowerFactor] = useState(0.89);
  const [current, setCurrent] = useState(125);
  const [voltage, setVoltage] = useState(380);
  const [lastStartAt, setLastStartAt] = useState<number | null>(null);
  const [inrushLogs, setInrushLogs] = useState<InrushLog[]>([]);
  const interlockMs = 5000;

  // Atualizar fator de potência baseado no status dos equipamentos
  useEffect(() => {
    const runningMotors = equipment.filter(e => e.type === 'motor' && e.status === 'running').length;
    const runningInverters = equipment.filter(e => e.type === 'inverter' && e.status === 'running').length;
    const capacitorBankRunning = equipment.find(e => e.type === 'capacitor')?.status === 'running';
    
    let newPowerFactor = 0.95; // Base
    if (runningMotors > 0) {
      newPowerFactor -= runningMotors * 0.05; // Motores diminuem o fator de potência
    }
    if (runningInverters > 0) {
      newPowerFactor -= runningInverters * 0.03; // Inversores também afetam
    }
    if (capacitorBankRunning) {
      newPowerFactor += 0.15; // Banco de capacitores melhora o fator de potência
    }
    
    setPowerFactor(Math.max(0.7, Math.min(0.99, newPowerFactor)));
  }, [equipment]);

  // Calcular corrente total e tensão média
  useEffect(() => {
    const runningEquipment = equipment.filter(e => e.status === 'running');
    const totalCurrent = runningEquipment.reduce((sum, e) => sum + e.current, 0);
    const avgVoltage = runningEquipment.length > 0 
      ? runningEquipment.reduce((sum, e) => sum + e.voltage, 0) / runningEquipment.length 
      : 380;
    setCurrent(totalCurrent);
    setVoltage(avgVoltage);
  }, [equipment]);

  // Efeito de emergência
  useEffect(() => {
    if (isEmergencyActive) {
      setEquipment(prev => prev.map(e => ({
        ...e,
        status: 'stopped',
        current: 0,
        power: 0,
        temperature: Math.max(25, e.temperature - 5)
      })));
      setCurrent(0);
    }
  }, [isEmergencyActive]);

  // Atualização periódica de temperatura e histórico quando equipamentos estão rodando
  useEffect(() => {
    const interval = setInterval(() => {
      if (!isEmergencyActive) {
        setEquipment(prev => prev.map(e => {
          const newHistory = [...e.history];
          const timestamp = Date.now();
          
          // Manter apenas últimos 50 registros (últimos 2.5 minutos)
          if (newHistory.length >= 50) {
            newHistory.shift();
          }
          
          if (e.status === 'running') {
            const variation = (Math.random() - 0.5) * 2;
            const newTemp = Math.max(25, Math.min(85, e.temperature + variation));
            
            newHistory.push({
              timestamp,
              current: e.current,
              voltage: e.voltage,
              power: e.power,
              temperature: newTemp
            });
            
            return {
              ...e,
              temperature: newTemp,
              history: newHistory
            };
          } else {
            const newTemp = Math.max(25, e.temperature - 0.5);
            
            newHistory.push({
              timestamp,
              current: 0,
              voltage: e.voltage,
              power: 0,
              temperature: newTemp
            });
            
            return {
              ...e,
              temperature: newTemp,
              history: newHistory
            };
          }
        }));
      }
    }, 3000);
    
    return () => clearInterval(interval);
  }, [isEmergencyActive]);

  const setEmergencyActive = (active: boolean) => {
    setIsEmergencyActive(active);
  };

  // Calcular corrente de inrush
  const calculateInrush = (equipment: Equipment): { inrush: number; factor: number } => {
    const nominalCurrent = equipment.nominalCurrent || 30;
    
    if (equipment.type === 'motor' || equipment.type === 'transformer') {
      // Equipamentos indutivos: I_inrush = k × I_nominal (k entre 6-10)
      const k = equipment.type === 'motor' ? 7 : 8; // Motores: 7x, Transformadores: 8x
      const inrush = k * nominalCurrent;
      return { inrush, factor: k };
    } else if (equipment.type === 'capacitor') {
      // Equipamentos capacitivos: I_inrush = V_rms × ω × C
      const Vrms = equipment.voltage;
      const f = 60; // Frequência da rede (Hz)
      const omega = 2 * Math.PI * f;
      const C = equipment.capacitance || 0.001; // Capacitância em Farads
      const inrush = Vrms * omega * C;
      const factor = inrush / nominalCurrent;
      return { inrush, factor };
    }
    
    // Outros tipos (inverter, generator) - inrush moderado
    return { inrush: nominalCurrent * 3, factor: 3 };
  };

  const updateEquipmentStatus = (id: string, status: 'running' | 'stopped'): boolean => {
    if (isEmergencyActive) return false;

    const target = equipment.find(e => e.id === id);
    if (!target) return false;

    // Trava de partida: impede iniciar motor se outra partida ocorreu recentemente
    if (status === 'running' && target.type === 'motor') {
      const now = Date.now();
      if (lastStartAt && now - lastStartAt < interlockMs) {
        return false;
      }
      setLastStartAt(now);
    }
    
    setEquipment(prev => prev.map(e => {
      if (e.id === id) {
        const newStatus = status;
        const baseValues: Record<string, { current: number; voltage: number; power: number }> = {
          '1': { current: 45, voltage: 380, power: 29.6 },
          '2': { current: 42, voltage: 380, power: 27.7 },
          '3': { current: 38, voltage: 380, power: 25.1 },
          '4': { current: 120, voltage: 13800, power: 2872 },
          '5': { current: 95, voltage: 13800, power: 2276 },
          '6': { current: 30, voltage: 380, power: 19.8 },
          '7': { current: 52, voltage: 380, power: 34.3 },
          '8': { current: 48, voltage: 380, power: 31.7 },
          '9': { current: 180, voltage: 380, power: 118.8 },
        };
        
        const base = baseValues[id] || { current: 30, voltage: 380, power: 20 };
        const nominalCurrent = e.nominalCurrent || base.current;
        
        // Calcular corrente de inrush usando fórmulas corretas
        const { inrush: inrushCurrent, factor: inrushFactor } = newStatus === 'running' 
          ? calculateInrush(e) 
          : { inrush: 0, factor: 0 };
        
        // Limite de alarme (inrush não deve exceder 12x a nominal)
        const alarmLimit = nominalCurrent * 12;
        const hasAlarm = inrushCurrent > alarmLimit;
        
        // Registrar energização no log
        if (newStatus === 'running') {
          const logEntry: InrushLog = {
            timestamp: Date.now(),
            equipmentId: e.id,
            equipmentName: e.name,
            inrushCurrent,
            nominalCurrent,
            inrushFactor,
            alarm: hasAlarm
          };
          
          setInrushLogs(prev => {
            const newLogs = [...prev, logEntry];
            // Manter apenas os últimos 100 registros
            return newLogs.slice(-100);
          });
          
          console.log(`[INRUSH] ${e.name}: ${inrushCurrent.toFixed(1)}A (${inrushFactor.toFixed(1)}x nominal) ${hasAlarm ? '⚠️ ALARME' : '✓'}`);
        }
        
        const result = {
          ...e,
          status: newStatus,
          current: newStatus === 'running' ? inrushCurrent : 0,
          voltage: base.voltage,
          power: newStatus === 'running' ? (base.power * inrushFactor) : 0,
          temperature: newStatus === 'running' ? e.temperature : Math.max(25, e.temperature - 10)
        };
        
        // Após 2 segundos, reduzir para corrente nominal (simulando fim do inrush)
        if (newStatus === 'running') {
          setTimeout(() => {
            setEquipment(prev2 => prev2.map(e2 => {
              if (e2.id === id && e2.status === 'running') {
                return { ...e2, current: nominalCurrent, power: base.power };
              }
              return e2;
            }));
          }, 2000);
        }
        
        return result;
      }
      return e;
    }));

    return true;
  };

  return (
    <ScadaContext.Provider value={{
      isEmergencyActive,
      setEmergencyActive,
      equipment,
      updateEquipmentStatus,
      powerFactor,
      current,
      voltage,
      interlockActive: !!(lastStartAt && Date.now() - lastStartAt < interlockMs),
      inrushLogs
    }}>
      {children}
    </ScadaContext.Provider>
  );
};

export const useScada = () => {
  const context = useContext(ScadaContext);
  if (!context) {
    throw new Error('useScada must be used within ScadaProvider');
  }
  return context;
};
