import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Shield, User, Lock } from 'lucide-react';
import logo from '@/assets/logo_engrenagem.png';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (login(username, password)) {
      navigate('/');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-primary/5 flex items-center justify-center p-4">
      <Card className="w-full max-w-md p-8 shadow-[var(--shadow-card)]">
        <div className="flex flex-col items-center mb-8">
          <img src={logo} alt="MattSchutz SCADA" className="h-24 mb-4" />
          <h1 className="text-2xl font-bold text-foreground mb-2">MattSchutz SCADA</h1>
          <p className="text-muted-foreground text-sm">Sistema de Controle Industrial</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-2">
            <Label htmlFor="username" className="flex items-center gap-2">
              <User className="h-4 w-4" />
              Usuário
            </Label>
            <Input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Digite seu usuário"
              required
              className="w-full"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password" className="flex items-center gap-2">
              <Lock className="h-4 w-4" />
              Senha
            </Label>
            <Input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Digite sua senha"
              required
              className="w-full"
            />
          </div>

          <Button type="submit" className="w-full bg-primary hover:bg-primary/90">
            <Shield className="h-4 w-4 mr-2" />
            Entrar no Sistema
          </Button>
        </form>

        <div className="mt-8 p-4 bg-muted/50 rounded-lg">
          <p className="text-xs text-muted-foreground mb-2 font-semibold">Usuários de Demonstração:</p>
          <div className="space-y-1 text-xs text-muted-foreground">
            <p>👤 <span className="font-mono">admin</span> / <span className="font-mono">admin123</span> - Administrador</p>
            <p>👤 <span className="font-mono">operador</span> / <span className="font-mono">op123</span> - Operador</p>
            <p>👤 <span className="font-mono">visitante</span> / <span className="font-mono">visit123</span> - Visitante</p>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default Login;
