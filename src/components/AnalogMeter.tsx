import { useEffect, useRef } from 'react';

interface AnalogMeterProps {
  value: number;
  maxValue: number;
  label: string;
  unit: string;
}

const AnalogMeter = ({ value, maxValue, label, unit }: AnalogMeterProps) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const size = 240;
    const centerX = size / 2;
    const centerY = size / 2 + 20;
    const radius = size * 0.35;

    // Obter cores do tema
    const getThemeColor = (cssVar: string): string => {
      const value = getComputedStyle(document.documentElement)
        .getPropertyValue(cssVar)
        .trim();
      if (!value) return '#000000';
      const parts = value.split(' ');
      if (parts.length === 3) {
        return `hsl(${parts[0]}, ${parts[1]}, ${parts[2]})`;
      }
      return `hsl(${value})`;
    };

    const getThemeColorAlpha = (cssVar: string, alpha: number): string => {
      const value = getComputedStyle(document.documentElement)
        .getPropertyValue(cssVar)
        .trim();
      if (!value) return `rgba(0, 0, 0, ${alpha})`;
      const parts = value.split(' ');
      if (parts.length === 3) {
        return `hsla(${parts[0]}, ${parts[1]}, ${parts[2]}, ${alpha})`;
      }
      return `hsla(${value}, ${alpha})`;
    };

    // Cores do tema
    const foreground = getThemeColor('--foreground');
    const border = getThemeColor('--border');
    const muted = getThemeColorAlpha('--muted', 0.15);
    const destructive = getThemeColor('--destructive');
    const background = getThemeColor('--background');

    // Limpar
    ctx.clearRect(0, 0, size, size);

    // Fundo
    ctx.fillStyle = muted;
    ctx.fillRect(0, 0, size, size);
    ctx.strokeStyle = border;
    ctx.lineWidth = 2;
    ctx.strokeRect(1, 1, size - 2, size - 2);

    // Título
    ctx.fillStyle = foreground;
    ctx.font = 'bold 15px system-ui, -apple-system, sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';
    ctx.fillText(label, centerX, 15);

    // Ângulos do arco (semicírculo superior)
    const startAngle = Math.PI * 0.75;
    const endAngle = Math.PI * 2.25;

    // Fundo do arco
    ctx.strokeStyle = getThemeColorAlpha('--border', 0.25);
    ctx.lineWidth = 14;
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, startAngle, endAngle);
    ctx.stroke();

    // Marcações e números
    ctx.lineWidth = 2.5;
    ctx.strokeStyle = foreground;
    ctx.fillStyle = foreground;

    const numMarks = 20;
    for (let i = 0; i <= numMarks; i++) {
      const angle = startAngle + (endAngle - startAngle) * (i / numMarks);
      const isMajor = i % 4 === 0;
      
      // Marcação
      const innerR = radius - 7;
      const outerR = isMajor ? radius + 14 : radius + 8;
      
      const x1 = centerX + Math.cos(angle) * innerR;
      const y1 = centerY + Math.sin(angle) * innerR;
      const x2 = centerX + Math.cos(angle) * outerR;
      const y2 = centerY + Math.sin(angle) * outerR;
      
      ctx.beginPath();
      ctx.moveTo(x1, y1);
      ctx.lineTo(x2, y2);
      ctx.stroke();

      // Números principais
      if (isMajor) {
        const textRadius = radius + 28;
        const tx = centerX + Math.cos(angle) * textRadius;
        const ty = centerY + Math.sin(angle) * textRadius;
        
        ctx.font = 'bold 12px system-ui, -apple-system, sans-serif';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        const markValue = (maxValue * i) / numMarks;
        ctx.fillText(markValue % 1 === 0 ? markValue.toFixed(0) : markValue.toFixed(1), tx, ty);
      }
    }

    // Ponteiro
    const clampedValue = Math.max(0, Math.min(value, maxValue));
    const needleAngle = startAngle + (endAngle - startAngle) * (clampedValue / maxValue);
    const needleLength = radius - 15;
    
    const needleEndX = centerX + Math.cos(needleAngle) * needleLength;
    const needleEndY = centerY + Math.sin(needleAngle) * needleLength;
    
    // Sombra do ponteiro
    ctx.shadowColor = 'rgba(0, 0, 0, 0.3)';
    ctx.shadowBlur = 5;
    ctx.shadowOffsetX = 2;
    ctx.shadowOffsetY = 2;
    
    // Desenhar ponteiro
    ctx.strokeStyle = destructive;
    ctx.lineWidth = 4;
    ctx.lineCap = 'round';
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(needleEndX, needleEndY);
    ctx.stroke();
    
    ctx.shadowColor = 'transparent';
    ctx.shadowBlur = 0;
    ctx.shadowOffsetX = 0;
    ctx.shadowOffsetY = 0;

    // Centro do ponteiro
    ctx.fillStyle = destructive;
    ctx.beginPath();
    ctx.arc(centerX, centerY, 8, 0, Math.PI * 2);
    ctx.fill();
    
    ctx.strokeStyle = background;
    ctx.lineWidth = 2.5;
    ctx.beginPath();
    ctx.arc(centerX, centerY, 8, 0, Math.PI * 2);
    ctx.stroke();

    // Display digital
    const digitalY = centerY + 52;
    
    ctx.fillStyle = foreground;
    ctx.font = 'bold 20px system-ui, -apple-system, sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    
    // Formatar valor baseado no tipo
    const displayValue = (label === 'Fator Potência' || label === 'Frequência') 
      ? value.toFixed(2) 
      : value.toFixed(0);
    
    ctx.fillText(displayValue, centerX, digitalY);
    
    ctx.font = '12px system-ui, -apple-system, sans-serif';
    ctx.fillStyle = getThemeColorAlpha('--muted-foreground', 1);
    ctx.fillText(unit, centerX, digitalY + 20);

  }, [value, maxValue, label, unit]);

  return (
    <div className="w-full h-full flex items-center justify-center">
      <canvas
        ref={canvasRef}
        width={240}
        height={240}
        className="max-w-full h-auto"
      />
    </div>
  );
};

export default AnalogMeter;
