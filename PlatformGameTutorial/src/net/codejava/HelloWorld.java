package net.codejava;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class HelloWorld {

	public static void main(String[] args) {
	
		
			// TODO Auto-generated method stub
			JFrame frame = new JFrame("PlatformerTutoriel");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.setLayout(new BorderLayout());
			frame.add(new GamePanel(), BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		
	}

}





	
