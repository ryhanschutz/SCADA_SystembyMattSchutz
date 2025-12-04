package com.mattschutz.scada.config;

import com.mattschutz.scada.entity.*;
import com.mattschutz.scada.repository.UserRepository;
import com.mattschutz.scada.service.EquipmentService;
import com.mattschutz.scada.service.HistoricalDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Random;

@Configuration
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
            motor1.setType(EquipmentType.MOTOR);
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
            motor1.setRatedPower(30.0);
            motor1.setRatedVoltage(380.0);
            motor1.setInsulationClass("F");
            motor1.setServiceType("S1");
            motor1.setCoolingType("IC411");
            motor1.setInstallationDate(LocalDateTime.now().minusMonths(6));
            equipmentService.save(motor1);

            // Motor Secundário
            Motor motor2 = new Motor();
            motor2.setName("Motor Bomba B2");
            motor2.setType(EquipmentType.MOTOR);
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
            motor2.setRatedPower(15.0);
            motor2.setRatedVoltage(380.0);
            motor2.setInsulationClass("F");
            motor2.setServiceType("S1");
            motor2.setCoolingType("IC411");
            motor2.setInstallationDate(LocalDateTime.now().minusMonths(4));
            equipmentService.save(motor2);

            // Transformador
            Transformer trafo = new Transformer();
            trafo.setName("Transformador Principal");
            trafo.setType(EquipmentType.TRANSFORMER);
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
            trafo.setRatedPowerKVA(500.0);
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
            capacitor.setPower(0.0);
            capacitor.setReactivePower(-150.0);
            capacitor.setTemperature(30.0 + random.nextDouble() * 10);
            capacitor.setCapacitance(150.0);
            capacitor.setInstallationDate(LocalDateTime.now().minusMonths(8));
            equipmentService.save(capacitor);

            // Inversor de Frequência
            Inverter inverter = new Inverter();
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
            inverter.setOutputFrequency(0.0);
            inverter.setFrequencySetpoint(60.0);
            inverter.setMinFrequency(10.0);
            inverter.setMaxFrequency(60.0);
            inverter.setMotorRatedPower(7.5);
            inverter.setMotorRatedCurrent(15.0);
            inverter.setInstallationDate(LocalDateTime.now().minusMonths(3));
            equipmentService.save(inverter);

            logger.info("Equipamentos criados com sucesso!");
        }
    }

    private void createHistoricalData() {
        logger.info("Criando dados históricos para demonstração...");
        
        try {
            equipmentService.findByStatus(EquipmentStatus.RUNNING).forEach(equipment -> {
                historicalDataService.createSampleData(equipment, 50);
            });
        } catch (Exception e) {
            logger.warn("Erro ao criar dados históricos: {}", e.getMessage());
        }
        
        logger.info("Dados históricos criados!");
    }
}
