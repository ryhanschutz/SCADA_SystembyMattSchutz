package com.mattschutz.scada.config;

import com.mattschutz.scada.entity.*;
import com.mattschutz.scada.repository.UserRepository;
import com.mattschutz.scada.service.EquipmentService;
import com.mattschutz.scada.service.HistoricalDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private HistoricalDataService historicalDataService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Iniciando inicialização de dados do sistema SCADA...");

        createDefaultUsers();
        createDefaultEquipment();
        createHistoricalData();

        logger.info("Inicialização de dados concluída com sucesso!");
    }

    private void createDefaultUsers() {
        if (userRepository.count() == 0) {
            logger.info("Criando usuários padrão...");

            // Administrador
            User admin = new User();
            admin.setUsername("admin");
            admin.setFullName("Administrador do Sistema");
            admin.setEmail("admin@mattschutz.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);

            // Operador
            User operator = new User();
            operator.setUsername("operador");
            operator.setFullName("Operador SCADA");
            operator.setEmail("operador@mattschutz.com");
            operator.setPassword(passwordEncoder.encode("op123"));
            operator.setRole(UserRole.OPERATOR);
            operator.setActive(true);
            userRepository.save(operator);

            // Supervisor
            User supervisor = new User();
            supervisor.setUsername("supervisor");
            supervisor.setFullName("Supervisor de Planta");
            supervisor.setEmail("supervisor@mattschutz.com");
            supervisor.setPassword(passwordEncoder.encode("sup123"));
            supervisor.setRole(UserRole.SUPERVISOR);
            supervisor.setActive(true);
            userRepository.save(supervisor);

            // Visitante
            User visitor = new User();
            visitor.setUsername("visitante");
            visitor.setFullName("Usuário Visitante");
            visitor.setEmail("visitante@mattschutz.com");
            visitor.setPassword(passwordEncoder.encode("visit123"));
            visitor.setRole(UserRole.VISITOR);
            visitor.setActive(true);
            userRepository.save(visitor);

            logger.info("Usuários criados: admin/admin123, operador/op123, supervisor/sup123, visitante/visit123");
        }
    }

    private void createDefaultEquipment() {
        if (equipmentService.findAll().isEmpty()) {
            logger.info("Criando equipamentos padrão...");
            Random random = new Random();

            // Motor Principal
            Motor motor1 = new Motor();
            motor1.setName("Motor Principal A1");
            motor1.setStatus(EquipmentStatus.STOPPED);
            motor1.setLocation("Linha de Produção 1");
            motor1.setManufacturer("WEG");
            motor1.setModel("W22 Premium");
            motor1.setSerialNumber("MT-001-2023");
            motor1.setNominalCurrent(45.0);
            motor1.setCurrent(0.0);
            motor1.setVoltage(380.0);
            motor1.setPower(0.0);
            motor1.setTemperature(25.0 + random.nextDouble() * 10);
            motor1.setRpm(0.0);
            motor1.setPoles(4);
            motor1.setInsulationClass("F");
            motor1.setServiceType("S1");
            motor1.setCoolingType("IC411");
            motor1.setInstallationDate(LocalDateTime.now().minusMonths(6));
            equipmentService.save(motor1);

            // Motor Secundário
            Motor motor2 = new Motor();
            motor2.setName("Motor Bomba B2");
            motor2.setStatus(EquipmentStatus.STOPPED);
            motor2.setLocation("Casa de Bombas");
            motor2.setManufacturer("WEG");
            motor2.setModel("W22 Standard");
            motor2.setSerialNumber("MT-002-2023");
            motor2.setNominalCurrent(25.0);
            motor2.setCurrent(0.0);
            motor2.setVoltage(380.0);
            motor2.setPower(0.0);
            motor2.setTemperature(23.0 + random.nextDouble() * 8);
            motor2.setRpm(0.0);
            motor2.setPoles(2);
            motor2.setInsulationClass("F");
            motor2.setServiceType("S1");
            motor2.setCoolingType("IC411");
            motor2.setInstallationDate(LocalDateTime.now().minusMonths(4));
            equipmentService.save(motor2);

            // Transformador
            Transformer trafo = new Transformer();
            trafo.setName("Transformador Principal");
            trafo.setStatus(EquipmentStatus.RUNNING);
            trafo.setLocation("Subestação Principal");
            trafo.setManufacturer("WEG");
            trafo.setModel("TTD 500kVA");
            trafo.setSerialNumber("TF-001-2022");
            trafo.setNominalCurrent(120.0);
            trafo.setCurrent(85.0 + random.nextDouble() * 20);
            trafo.setVoltage(380.0);
            trafo.setPower(280.0 + random.nextDouble() * 40);
            trafo.setTemperature(45.0 + random.nextDouble() * 15);
            trafo.setPrimaryVoltage(13800.0);
            trafo.setSecondaryVoltage(380.0);
            trafo.setConnectionType("Delta-Estrela");
            trafo.setCoolingType("ONAN");
            trafo.setOilLevel(0.85);
            trafo.setOilTemperature(55.0);
            trafo.setInstallationDate(LocalDateTime.now().minusYears(2));
            equipmentService.save(trafo);

            // Banco de Capacitores
            Equipment capacitor = new Equipment();
            capacitor.setName("Banco Capacitores");
            capacitor.setType(EquipmentType.CAPACITOR);
            capacitor.setStatus(EquipmentStatus.RUNNING);
            capacitor.setLocation("Subestação Principal");
            capacitor.setManufacturer("WEG");
            capacitor.setModel("CCM-300kVAr");
            capacitor.setSerialNumber("CP-001-2023");
            capacitor.setNominalCurrent(30.0);
            capacitor.setCurrent(28.0 + random.nextDouble() * 4);
            capacitor.setVoltage(380.0);
            capacitor.setPower(0.0); // Capacitor não consome potência ativa
            capacitor.setReactivePower(-150.0); // Fornece potência reativa
            capacitor.setTemperature(30.0 + random.nextDouble() * 10);
            capacitor.setCapacitance(150.0); // uF
            capacitor.setInstallationDate(LocalDateTime.now().minusMonths(8));
            equipmentService.save(capacitor);

            // Inversor de Frequência
            Equipment inverter = new Equipment();
            inverter.setName("Inversor CFW-300");
            inverter.setType(EquipmentType.INVERTER);
            inverter.setStatus(EquipmentStatus.STOPPED);
            inverter.setLocation("Painel Principal");
            inverter.setManufacturer("WEG");
            inverter.setModel("CFW300A07P6T4NB20");
            inverter.setSerialNumber("IV-001-2023");
            inverter.setNominalCurrent(15.0);
            inverter.setCurrent(0.0);
            inverter.setVoltage(380.0);
            inverter.setPower(0.0);
            inverter.setTemperature(35.0 + random.nextDouble() * 15);
            inverter.setInstallationDate(LocalDateTime.now().minusMonths(3));
            equipmentService.save(inverter);

            logger.info("Equipamentos criados com sucesso!");
        }
    }

    private void createHistoricalData() {
        logger.info("Criando dados históricos para demonstração...");
        
        // Criar dados históricos para equipamentos em funcionamento
        try {
            equipmentService.findByStatus(EquipmentStatus.RUNNING).forEach(equipment -> {
                createSampleHistoricalData(equipment);
            });
        } catch (Exception e) {
            logger.warn("Erro ao criar dados históricos: {}", e.getMessage());
        }
        
        logger.info("Dados históricos criados!");
    }

    private void createSampleHistoricalData(Equipment equipment) {
        try {
            Random random = new Random();
            LocalDateTime now = LocalDateTime.now();
            
            // Criar 50 pontos de dados nas últimas 2 horas
            for (int i = 50; i >= 0; i--) {
                HistoricalData historical = new HistoricalData();
                historical.setEquipment(equipment);
                historical.setTimestamp(now.minusMinutes(i * 2)); // A cada 2 minutos
                
                // Dados baseados no tipo de equipamento
                switch (equipment.getType()) {
                    case MOTOR:
                        historical.setCurrent(equipment.getNominalCurrent() * (0.7 + random.nextDouble() * 0.4));
                        historical.setVoltage(380.0 + random.nextGaussian() * 5);
                        historical.setPower(equipment.getNominalCurrent() * 0.7 * 380 * Math.sqrt(3) / 1000 * (0.8 + random.nextDouble() * 0.3));
                        historical.setTemperature(35.0 + random.nextDouble() * 20);
                        break;
                        
                    case TRANSFORMER:
                        historical.setCurrent(equipment.getCurrent() + random.nextGaussian() * 5);
                        historical.setVoltage(380.0 + random.nextGaussian() * 3);
                        historical.setPower(equipment.getPower() + random.nextGaussian() * 20);
                        historical.setTemperature(45.0 + random.nextDouble() * 20);
                        break;
                        
                    case CAPACITOR:
                        historical.setCurrent(equipment.getCurrent() + random.nextGaussian() * 2);
                        historical.setVoltage(380.0 + random.nextGaussian() * 2);
                        historical.setPower(0.0);
                        historical.setTemperature(30.0 + random.nextDouble() * 15);
                        break;
                        
                    default:
                        historical.setCurrent(10.0 + random.nextDouble() * 20);
                        historical.setVoltage(380.0 + random.nextGaussian() * 5);
                        historical.setPower(5.0 + random.nextDouble() * 15);
                        historical.setTemperature(25.0 + random.nextDouble() * 25);
                        break;
                }
                
                historical.setActivePower(historical.getPower());
                historical.setReactivePower(equipment.getType() == EquipmentType.CAPACITOR ? -50.0 : historical.getPower() * 0.4);
                historical.setSource("automatic");
                
                historicalDataService.save(historical);
            }
        } catch (Exception e) {
            logger.warn("Erro ao criar dados históricos para equipamento {}: {}", 
                equipment.getName(), e.getMessage());
        }
    }
}
