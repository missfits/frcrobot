
package org.usfirst.frc.team6418.robot;
import java.util.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.UsbCamera;
//import org.usfirst.frc.team6418.robot.commands.ExampleCommand;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;

import org.usfirst.frc.team6418.robot.subsystems.*;
import org.opencv.core.*;
import org.usfirst.frc.team6418.robot.commands.*;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	
	public static final int WINCHMOTOR = 2;
	//public static OI oi;
	public static DriveTrain driveTrain;
	//public static RobotDrive driveTrain;
	public static Winch winch;
	public static GreenLightSystem greenlight = new GreenLightSystem();
	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();
	public static CameraServer cameraServer;
	//Testing UI, probably should put all joystick and buttons in a separate OI class
	public Joystick stick = new Joystick(0);
	public JoystickButton AButton = new JoystickButton(stick,1); 
	public JoystickButton BButton = new JoystickButton(stick,2);
	public JoystickButton StartButton = new JoystickButton(stick,8);
	
	//use Button A and Button B, togggleWhenPressed(final Command command) 
	//to toggle between arcadeDrive and tankDrive
	//use StartButton to turn the green light on and off
	
	public static String driveMode;
	//Sensors
	public static UltraSonic s1;
	
	//Encoders
	public Encoder enc;
	public Encoder enc2;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//oi = new OI();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		
		//driveTrain = new RobotDrive(0,1);
		driveTrain = new DriveTrain(0,1);
		winch = new Winch(WINCHMOTOR, 9);
		//how to call: public void tankDrive(double x, double y){
		//winch.tankDrive(stick.getRawAxis(2), 0);
		
		
		//Left Encoder
		int b = 1;
		int c = 2;
		enc = new Encoder(b, c);
		enc.setMinRate(10);
		enc.setDistancePerPulse(5);
		enc.setSamplesToAverage(7);
		
		//Right Encoder
		int d = 3;
		int e = 4;
		enc2 = new Encoder(d, e, true);
		enc2.setMinRate(10);
		enc2.setDistancePerPulse(5);
		enc2.setSamplesToAverage(7);
		
		
		
		//Testing Sensor...
		s1 = new UltraSonic(0);
		//Testing Video ... 
//		new Thread(() -> {
//            UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
//            camera.setResolution(640, 480);
//            
//            CvSink cvSink = CameraServer.getInstance().getVideo();
//            CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
//            
//            Mat source = new Mat();
//            Mat output = new Mat();
//            //List<Mat> bgr = new Vector<Mat>();
//            
//            while(!Thread.interrupted()) {
//                cvSink.grabFrame(source);
//              //  Core.split(source,bgr );
//                Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
//                Imgproc.threshold(output, output, 150, 255, Imgproc.THRESH_TOZERO);
//                outputStream.putFrame(output);
//            }
//        }).start();
		
		//switching between drive modes - Halie 
		
		driveMode ="ARCADE";
		//if a is pressed, driveMode = "ARCADE"
		//if b is pressed, diveMode= "TANK"
		//togglewhenpressed
		AButton.whenPressed(new SetTankDrive());
		BButton.whenPressed(new SetArcadeDrive());
		
		StartButton.whenPressed(new GreenLight());
		
		
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */
		//autonomousCommand = new MoveForward();
		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		System.out.println("Initiating Teleop Mode");

		if (autonomousCommand != null)
			autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
//needs a String/boolean parameter		
		Scheduler.getInstance().run();

		//double v = s1.getDistance();

		
//		System.out.println(v);
		
		//tank drive - Aliya / Halie 
		
		if (driveMode.equalsIgnoreCase("TANK")) {

			driveTrain.tankDrive(-1*stick.getY(),-1*stick2.getY());
		}
		//left axis: stick.getY();
		//right axis: stick.getRawAxis(5);

		else if(driveMode.equalsIgnoreCase("ARCADE")){
		//arcade drive
			driveTrain.arcadeDrive(-stick.getY(),stick.getX());
		}
		
		//winch control - Halie 
		winch.tankDrive(stick.getRawAxis(2), 0);
		
		
		//Encoders - Aliya
		String enc_name = "left encoder: ";
		String enc2_name = "right encoder: ";
		System.out.println(enc_name + enc.get());
		System.out.println(enc2_name + enc2.get());
		
		//winch read output - Halie 
		System.out.println("Left Trigger" + stick.getRawAxis(2));
		
		
		
		
		//System.out.println("left  = " + -100*stick.getY());
	//	System.out.println("right  = " + -100*stick.getRawAxis(5));


	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
	 
}

