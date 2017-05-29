package com.USB_LED;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;

public class UsbLedControl extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	JPanel panelCont = new JPanel();
	JPanel ledCont = new JPanel();

	JButton led1 = new CircleButton("");
	JButton led2 = new CircleButton("");
	JButton led3 = new CircleButton("");
	JButton led4 = new CircleButton("");

	boolean light1 = false;
	boolean light2 = false;
	boolean light3 = false;
	boolean light4 = false;

	private void WindowInit() {
		this.setTitle("USB LED Control GUI");
		this.setSize(500, 500);
		this.setLocation(200, 200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		/*led1.setBackground(Color.BLACK);
		led1.setPreferredSize(new Dimension(50,300));
		led2.setPreferredSize(new Dimension(50,300));
		
		panelCont.setLayout(new BoxLayout(panelCont,BoxLayout.Y_AXIS));	
		
		ledCont.setLayout(new BoxLayout(ledCont,BoxLayout.X_AXIS));
		ledCont.add(Box.createHorizontalStrut (100));
		ledCont.add(led1);	
		ledCont.add(Box.createHorizontalGlue ());  
		ledCont.add(led2);
		ledCont.add(Box.createHorizontalStrut (100));
		
		panelCont.add(Box.createVerticalStrut (100)); 
		panelCont.add(ledCont); 
		panelCont.add(Box.createVerticalStrut (100)); */

		this.setLayout(null);
		int sizeW = 50, spceX = 50;
		int iniX = 50, iniY = 50;
		led1.setBounds(iniX, iniY, sizeW, sizeW);
		led2.setBounds(iniX + sizeW + spceX, iniY, sizeW, sizeW);
		led3.setBounds(iniX + 2 * (sizeW + spceX), iniY, sizeW, sizeW);
		led4.setBounds(iniX + 3 * (sizeW + spceX), iniY, sizeW, sizeW);

		led1.setBackground(Color.black);
		led2.setBackground(Color.black);
		led3.setBackground(Color.black);
		led4.setBackground(Color.black);

		this.add(led1);
		this.add(led2);
		this.add(led3);
		this.add(led4);

		led1.addActionListener((ActionListener) this);
		led2.addActionListener((ActionListener) this);
		led3.addActionListener((ActionListener) this);
		led4.addActionListener((ActionListener) this);
	}

	public void actionPerformed(ActionEvent e) {
		JButton jb = (JButton) e.getSource();
		if (jb == led1)
			light1 = !light1;
		if (jb == led2)
			light2 = !light2;
		if (jb == led3)
			light3 = !light3;
		if (jb == led4)
			light4 = !light4;
		this.paint();

	}

	private void paint() {
		if (light1)
			led1.setBackground(Color.red);
		else
			led1.setBackground(Color.black);

		if (light2)
			led2.setBackground(Color.red);
		else
			led2.setBackground(Color.black);

		if (light3)
			led3.setBackground(Color.red);
		else
			led3.setBackground(Color.black);

		if (light4)
			led4.setBackground(Color.red);
		else
			led4.setBackground(Color.black);

		int li1 = (light1) ? 1 : 0;
		int li2 = (light2) ? 1 : 0;
		int li3 = (light3) ? 1 : 0;
		int li4 = (light4) ? 1 : 0;
		byte light[] = new byte[1];
		light[0] = (byte) (((li4 << 3) | (li3 << 2) | (li2 << 1) | (li1 << 0)) & 0x0f);
		//System.out.print(Byte.toString(light[0]));
		UsbComm usbComm = new UsbComm();
		usbComm.sent(light);
		usbComm.release();

		this.repaint();
	}

	private UsbLedControl() {
		this.WindowInit();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new UsbLedControl();
	}

	public class CircleButton extends JButton {
		private static final long serialVersionUID = 1L;

		public CircleButton(String label) {
			super(label);
			//Dimension size = getPreferredSize();
			//size.width = size.height = Math.max(size.width, size.height);
			//setPreferredSize(size);
			setContentAreaFilled(false);
		}

		protected void paintComponent(Graphics g) {
			if (getModel().isArmed()) {
				g.setColor(Color.lightGray);
			} else {
				g.setColor(getBackground());
			}
			g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
			super.paintComponent(g);
		}

		protected void paintBorder(Graphics g) {
			g.setColor(Color.white);
			g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
		}

		Shape shape;

		public boolean contains(int x, int y) {
			if ((shape == null) || (!shape.getBounds().equals(getBounds()))) {
				shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
			}
			return shape.contains(x, y);
		}
	}

	public class UsbComm {
		public short idV = 0x04b4;//Vendor ID
		public short idP = 0x1004;//Product ID

		public byte INTERFACE = 0;
		DeviceHandle handle1 = null;

		public UsbComm() {
			int tempR1 = LibUsb.init(null);
			if (tempR1 != LibUsb.SUCCESS) {
				System.out.print("Unable to initialize" + "\n");
			}

			handle1 = LibUsb.openDeviceWithVidPid(null, idV, idP);
			if (handle1 == null) {
				System.out.print("Unable to open device" + "\n");
			}

			int r = LibUsb.detachKernelDriver(handle1, INTERFACE);
			if (r != LibUsb.SUCCESS && r != LibUsb.ERROR_NOT_SUPPORTED && r != LibUsb.ERROR_NOT_FOUND)
				System.out.print("Unable to detachUSB" + "\n");
			tempR1 = LibUsb.claimInterface(handle1, INTERFACE);
			if (tempR1 != LibUsb.SUCCESS)
				System.out.print("Unable get interface" + "\n");
		}

		public boolean sent(byte[] text) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(text.length);
			buffer.put(text);
			IntBuffer transfered = IntBuffer.allocate(3);
			int result = LibUsb.bulkTransfer(handle1, (byte) 0x01, buffer, transfered, 3000);
			if (result != LibUsb.SUCCESS) {
				System.out.println("EXCEPTION THROWN");
				return false;
			}
			result = LibUsb.bulkTransfer(handle1, (byte) 0x81, buffer, transfered, 3000);
			if (result != LibUsb.SUCCESS) {
				System.out.println("EXCEPTION THROWN");
				return false;
			}
			return true;
		}

		public void release() {
			int tempR1 = LibUsb.releaseInterface(handle1, 0);
			if (tempR1 != LibUsb.SUCCESS)
				System.out.print("Unable to release" + "\n");
			LibUsb.close(handle1);

		}

	}

}
