package ks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.json.simple.parser.ParseException;
import java.awt.Font;

public class DownloadingProcess extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JProgressBar progressBar;
	private DownloadingProcess proc = this;
	private static JDialog dialog = new JDialog();
	
	private JProgressBar setProgressBar() { // встановлення прогрес бару для показу статусу завантаження
		
   	 	JProgressBar progressBar = new JProgressBar();
   	 	
        progressBar.setStringPainted(true);
        progressBar.setBounds(10, 41, 338, 35);
        progressBar.setMinimum(0);
        progressBar.setForeground(Color.decode("#1AB221"));
        progressBar.setMaximum(100);
        
        return progressBar;
    }
	
	public DownloadingProcess(final String TOKEN_ACCESS, final String USER_ID, String path, boolean[] multimedia, 
			final Thread downloading, String methodType, JTextPane downloadVideoLink) 
												throws URISyntaxException, IOException, ParseException, InterruptedException { // конструктор класу

		super(dialog, "Завантаження");
		
		JLabel label = new JLabel("");
		dialog.getContentPane().add(label, BorderLayout.SOUTH);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setBounds(400, 200, 374, 156);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 368, 95);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);		
		
		progressBar = setProgressBar();
		contentPanel.add(progressBar);
		
		JLabel lblProgressOfChecking = new JLabel("Хід завантаження");
		lblProgressOfChecking.setHorizontalAlignment(SwingConstants.CENTER);
		lblProgressOfChecking.setBounds(10, 11, 338, 19);
		contentPanel.add(lblProgressOfChecking);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(108, 81, 130, 14);
		contentPanel.add(lblNewLabel);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setBounds(0, 95, 368, 33);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane);
		
		JButton cancelButton = new JButton("Стоп");
		cancelButton.setActionCommand("Стоп");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					downloading.stop();
					proc.dispose();
				} catch(Exception ex) {}
			}
		});
		
		try {
			VKDownloader.download(TOKEN_ACCESS, USER_ID, 6000, 0, path, multimedia, progressBar, lblProgressOfChecking, lblNewLabel, methodType, downloadVideoLink);
			this.dispose();
		} catch(Exception e) { e.printStackTrace(); }
	}
}