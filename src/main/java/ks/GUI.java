package ks;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;

import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.List;
import javax.swing.JSeparator;

public class GUI extends JFrame {

	private JPanel contentPane, audioPanel, videoPanel;
	private JButton downloadSelectedAudioButton, downloadSelectedVideoButton, downloadAllAudioButton, chooseDestinationAudioButton, clearListAudioButton, clearListVideoButton;
	private JLabel folderAudioDestinationLabel, accessAudioLabel, accessVideoLabel, vkAudiolistLabel, vkVideolistLabel, audioLink, videoLink, a, b, labelAudio, labelVideo;
	private JTextPane linkAudio, linkVideo, downloadVideoLink;
	private JTextField textAudio, textVideo;
	private String TOKEN_ACCESS = null, USER_ID = null, path = "";
	private boolean isAccesAllowed = false, isFolderDestinationChoosed = false;
	private final Canvas canvasAudio, canvasVideo;
	private List vkAudioList, vkVideoList, listToAudioDownload, listToVideoDownload;
	private boolean[] songsToDownload, videosToDownload;
	private int downloadAudioListCounter = 1, downloadVideoListCounter = 1;
	private JSeparator separator_1, separator_2, separator_3, separator_4;
	private Thread downloading;
	private String[] audiolist, videolist;
	
	protected int count = 0;

	public GUI() { // конструктор класу
		
		super("VKDownloader RR");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(275, 5, 780, 750);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);;
		contentPane.setBackground(new Color (0, 250, 154));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 755, 700);
		contentPane.add(tabbedPane);
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBackground(new Color (238, 232, 170));
		
		tabbedPane.addTab("Інструкція", null, settingsPanel, null);
		settingsPanel.setLayout(null);
		
		linkAudio = new JTextPane();
		linkAudio.setFont(new Font("Tahoma", Font.PLAIN, 14));
		linkAudio.setEditable(false);
		linkAudio.setText("https://oauth.vk.com/authorize?client_id=5470640&scope=audio& redirect_uri=https://oauth.vk.com/blank.html&display=page&v=5.0&response_type=token");
		linkAudio.setBounds(35, 90, 570, 40);
		linkAudio.setBackground(Color.YELLOW);
		settingsPanel.add(linkAudio);
		
		audioLink = new JLabel ();
		audioLink.setFont(new Font("Tahoma", Font.PLAIN, 14));
		audioLink.setText("<html>Або натисніть на це посилання: <a href=https://oauth.vk.com/authorize?client_id=5470640&scope=audio&redirect_uri=https://oauth.vk.com/blank.html&display=popup&v=5.7&response_type=token>click</a> </html>");
		audioLink.setBounds(35, 110, 240, 60);
		
		audioLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://oauth.vk.com/authorize?client_id=5470640&scope=audio&redirect_uri=https://oauth.vk.com/blank.html&display=popup&v=5.7&response_type=token"));
                } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage() , "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        settingsPanel.add(audioLink);
		
		linkVideo = new JTextPane();
		linkVideo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		linkVideo.setEditable(false);
		linkVideo.setText("https://oauth.vk.com/authorize?client_id=5470640&scope=video& redirect_uri=https://oauth.vk.com/blank.html&display=page&v=5.0&response_type=token");
		linkVideo.setBounds(35, 190, 570, 40);
		linkVideo.setBackground(Color.YELLOW);
		settingsPanel.add(linkVideo);
		
		videoLink = new JLabel ();
		videoLink.setFont(new Font("Tahoma", Font.PLAIN, 14));
		videoLink.setText("<html>Або натисніть на це посилання: <a href=https://oauth.vk.com/authorize?client_id=5470640&scope=video&redirect_uri=https://oauth.vk.com/blank.html&display=popup&v=5.7&response_type=token>click</a></html>");
		videoLink.setBounds(35, 210, 240, 60);
		
		videoLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://oauth.vk.com/authorize?client_id=5470640&scope=video&redirect_uri=https://oauth.vk.com/blank.html&display=popup&v=5.7&response_type=token"));
                    } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage() , "Помилка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        
        settingsPanel.add(videoLink);
		
		JLabel linkLabel = new JLabel("<html><pre><font size = 5 color = black>1. Скопіюйте вказане нижче посилання та вставте в адресну стрічку<br>   браузера:</font></pre></html>");
		linkLabel.setBounds(10, 10, 830, 50);
		settingsPanel.add(linkLabel);
		
		a = new JLabel("<html><pre><font size = 5 color = black>а) Для отримання доступу для аудіофайлів:</font></pre></html>");
		a.setFont(new Font("Arial", Font.PLAIN, 15));
		a.setBounds(10, 60, 500, 20);
		settingsPanel.add(a);
		
		b = new JLabel("<html><pre><font size = 5 color = black>б) Для отримання доступу для відеофайлів:</font></pre></html>");
		b.setFont(new Font("Arial", Font.PLAIN, 15));
		b.setBounds(10, 160, 500, 20);
		settingsPanel.add(b);
		
		JLabel accessLabel = new JLabel("<html><pre><font size = 5 color = black>2. На завантаженій сторінці дозвольте доступ до Ваших записів<br>   (Цей крок виконується лише раз).</font></pre><html>");
		accessLabel.setBounds(10, 270, 740, 50);
		settingsPanel.add(accessLabel);
		
		JLabel lblNewLabel = new JLabel("<html><pre><font size = 5 color = black>3. Вас перенаправлено на іншу веб-сторінку. Скопіюйте посилання з<br>   адресної стрічки, вставте у відповідне поле та натисніть Enter:</font></pre><html>");
		lblNewLabel.setBounds(10, 330, 740, 50);
		settingsPanel.add(lblNewLabel);
		
		textAudio = new JTextField();
		textAudio.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textAudio.setBounds(30, 390, 570, 34);
		textAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getActionCommand().equals("")) return;
				
				try {
					TOKEN_ACCESS = getTOKEN_ACCESS(e.getActionCommand());
					USER_ID = getUSER_ID(e.getActionCommand());
					canvasAudio.setBackground(Color.GREEN);
					isAccesAllowed = true;
					accessAudioLabel.setText("Доступ до аудіозаписів отримано!");				

					audiolist = Audiolist.getAudiolist(TOKEN_ACCESS, USER_ID);
					songsToDownload = new boolean[audiolist.length];
					int i = 1;
					
					for(String temp : audiolist) {
						vkAudioList.add(i++ + ". " + temp);
					}
					
					downloadAllAudioButton.setEnabled(true);
					downloadSelectedAudioButton.setEnabled(true);
					clearListAudioButton.setEnabled(true);
					chooseDestinationAudioButton.setEnabled(true);
				} catch(Exception ex) {
					System.out.println(ex);
					isAccesAllowed = false;
					canvasAudio.setBackground(Color.RED);
					accessAudioLabel.setText("Доступ до аудіозаписів заборонено!");					
					errorBox("Невірне посилання!","Помилка");
				}
				textAudio.setText("");
			}
		});
		
		textVideo = new JTextField();
		textVideo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textVideo.setBounds(30, 530, 570, 34);
		textVideo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getActionCommand().equals("")) return;
				
				try {
					TOKEN_ACCESS = getTOKEN_ACCESS(e.getActionCommand());
					USER_ID = getUSER_ID(e.getActionCommand());
					canvasVideo.setBackground(Color.GREEN);
					isAccesAllowed = true;
					accessVideoLabel.setText("Доступ до відеозаписів отримано!");				

					videolist = Videolist.getVideolist(TOKEN_ACCESS, USER_ID);
					videosToDownload = new boolean[videolist.length];
					int i = 1;
					
					for(String temp : videolist) {
						vkVideoList.add(i++ + ". " + temp);
					}
					
					downloadSelectedVideoButton.setEnabled(true);
					clearListVideoButton.setEnabled(true);
				} catch(Exception ex) {
					isAccesAllowed = false;
					canvasVideo.setBackground(Color.RED);
					accessVideoLabel.setText("Доступ до відеозаписів заборонено!");					
					errorBox("Невірне посилання!","Помилка");
				}
				textVideo.setText("");
			}
		});
		
		settingsPanel.add(textAudio);
		settingsPanel.add(textVideo);
		
		canvasAudio = new Canvas();
		canvasAudio.setFont(new Font("Tahoma", Font.PLAIN, 12));
		canvasAudio.setForeground(Color.BLACK);
		canvasAudio.setBackground(Color.RED);
		canvasAudio.setBounds(30, 430, 305, 60);
		settingsPanel.add(canvasAudio);
		
		canvasVideo = new Canvas();
		canvasVideo.setFont(new Font("Tahoma", Font.PLAIN, 12));
		canvasVideo.setForeground(Color.BLACK);
		canvasVideo.setBackground(Color.RED);
		canvasVideo.setBounds(30, 570, 305, 60);
		settingsPanel.add(canvasVideo);
		
		accessAudioLabel = new JLabel("Доступ до аудіозаписів заборонено!");
		accessAudioLabel.setFont(new Font("Arial", Font.BOLD, 14));
		accessAudioLabel.setBounds(340, 410, 260, 100);
		settingsPanel.add(accessAudioLabel);
		
		accessVideoLabel = new JLabel("Доступ до відеозаписів заборонено!");
		accessVideoLabel.setFont(new Font("Arial", Font.BOLD, 14));
		accessVideoLabel.setBounds(340, 550, 260, 100);
		settingsPanel.add(accessVideoLabel);
		
		audioPanel = new JPanel();
		audioPanel.setBackground(new Color (238, 232, 170));
		tabbedPane.addTab("Аудіозаписи користувача", null, audioPanel, null);
		audioPanel.setLayout(null);
		
		videoPanel = new JPanel();
		videoPanel.setBackground(new Color (238, 232, 170));
		tabbedPane.addTab("Відеозаписи користувача", null, videoPanel, null);
		videoPanel.setLayout(null);
		
		JLabel lbl1 = new JLabel("<html><pre><font size = 5 color = black>1. Скопіюйте отримане нижче посилання та вставте в адресну стрічку<br>   браузера:</font></pre></html>");
		lbl1.setBounds(10, 520, 830, 50);
		videoPanel.add(lbl1);
		
		JLabel lbl2 = new JLabel("<html><pre><font size = 5 color = black>2. Натисніть ПКМ та виберіть \"Завантажити\" <br>   або комбінацію клавіш CTRL + S</font></pre></html>");
		lbl2.setBounds(10, 570, 830, 50);
		videoPanel.add(lbl2);
		
		downloadVideoLink = new JTextPane();
		downloadVideoLink.setFont(new Font("Tahoma", Font.PLAIN, 14));
		downloadVideoLink.setEditable(false);
		downloadVideoLink.setText("");
		downloadVideoLink.setBounds(40, 630, 570, 30);
		downloadVideoLink.setBackground(Color.YELLOW);
		videoPanel.add(downloadVideoLink);
		
		folderAudioDestinationLabel = new JLabel("Завантаження у папку");
		folderAudioDestinationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		folderAudioDestinationLabel.setBounds(300, 545, 460, 40);
		audioPanel.add(folderAudioDestinationLabel);

		vkAudioList = new List();
		vkAudioList.setBounds(10, 27, 730, 200);
		vkAudioList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(e.getClickCount() == 2) {
					try {
						if(songsToDownload[vkAudioList.getSelectedIndex()] == true) return;
						listToAudioDownload.add(downloadAudioListCounter++ + ". " 
								+ vkAudioList.getSelectedItem().substring(getNumberOfDigits(vkAudioList.getSelectedIndex()) + 2));
						songsToDownload[vkAudioList.getSelectedIndex()] = true;
					} catch(Exception ex){};
				}
			}
		});
		
		vkVideoList = new List();
		vkVideoList.setBounds(10, 27, 730, 200);
		vkVideoList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(e.getClickCount() == 2) {
					try {
						if(videosToDownload[vkVideoList.getSelectedIndex()] == true) return;
						listToVideoDownload.add(downloadVideoListCounter++ + ". " 
								+ vkVideoList.getSelectedItem().substring(getNumberOfDigits(vkVideoList.getSelectedIndex()) + 2));
						videosToDownload[vkVideoList.getSelectedIndex()] = true;
					} catch(Exception ex){};
				}
			}
		});
		
		audioPanel.add(vkAudioList);
		videoPanel.add(vkVideoList);
		
		listToAudioDownload = new List();
		listToAudioDownload.setBounds(10, 258, 730, 199);
		listToAudioDownload.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(e.getClickCount() == 2) {
					try {
						int i = listToAudioDownload.getSelectedIndex();
						listToAudioDownload.remove(i);
						
						for(int j = i, length = listToAudioDownload.getItemCount(); j < length; j++){
							String temp = listToAudioDownload.getItem(j);
							listToAudioDownload.remove(j);
							listToAudioDownload.add((j + 1) + ". " + temp.substring(getNumberOfDigits(j + 2) + 2), j);
						}							
						songsToDownload[i] = false;
						downloadAudioListCounter--;
					} catch(Exception ex){};
				}
			}
		});
		
		listToVideoDownload = new List();
		listToVideoDownload.setBounds(10, 258, 730, 199);
		listToVideoDownload.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(e.getClickCount() == 2) {
					try {
						int i = listToVideoDownload.getSelectedIndex();
						listToVideoDownload.remove(i);
						
						for(int j = i, length = listToVideoDownload.getItemCount(); j < length; j++){
							String temp = listToVideoDownload.getItem(j);
							listToVideoDownload.remove(j);
							listToVideoDownload.add((j + 1) + ". " + temp.substring(getNumberOfDigits(j + 2) + 2), j);
						}							
						videosToDownload[i] = false;
						downloadVideoListCounter--;
					} catch(Exception ex){};
				}
			}
		});
		
		audioPanel.add(vkAudioList);
		audioPanel.add(listToAudioDownload);
		
		videoPanel.add(vkVideoList);
		videoPanel.add(listToVideoDownload);
		
		vkAudiolistLabel = new JLabel("Усі аудіозаписи");
		vkAudiolistLabel.setHorizontalAlignment(SwingConstants.LEFT);
		vkAudiolistLabel.setBounds(20, 0, 120, 30);
		audioPanel.add(vkAudiolistLabel);
		
		vkVideolistLabel = new JLabel("Усі відеозаписи");
		vkVideolistLabel.setHorizontalAlignment(SwingConstants.LEFT);
		vkVideolistLabel.setBounds(20, 0, 120, 30);
		videoPanel.add(vkVideolistLabel);
		
		separator_1 = new JSeparator();
		separator_1.setBounds(10, 235, 730, 2);
		audioPanel.add(separator_1);
		
		separator_2 = new JSeparator();
		separator_2.setBounds(10, 235, 730, 2);
		videoPanel.add(separator_2);
		
		separator_3 = new JSeparator();
		separator_3.setBounds(10, 465, 730, 2);
		audioPanel.add(separator_3);
		
		separator_4 = new JSeparator();
		separator_4.setBounds(10, 465, 730, 2);
		videoPanel.add(separator_4);
		
		labelAudio = new JLabel ("Аудіозаписи для завантаження");
		labelAudio.setHorizontalAlignment(SwingConstants.LEFT);
		labelAudio.setBounds(20, 230, 200, 30);
		audioPanel.add(labelAudio);
		
		labelVideo = new JLabel ("Відеозаписи для завантаження");
		labelVideo.setHorizontalAlignment(SwingConstants.LEFT);
		labelVideo.setBounds(20, 230, 200, 30);
		videoPanel.add(labelVideo);
		
		downloadAllAudioButton = new JButton("Завантажити всі аудіозаписи");
		downloadAllAudioButton.setBounds(255, 475, 210, 30);
		downloadAllAudioButton.setEnabled(false);
		
		downloadAllAudioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				boolean[] array = new boolean[audiolist.length];
				
				for(int i = 0; i < array.length; i++) {
					array[i] = true;
				}
				
				downloading = new Thread(new Downloading(array, true, "/method/audio.get", downloadVideoLink));
				downloading.start();
			}
		});
		
		audioPanel.add(downloadAllAudioButton);
		
		downloadSelectedAudioButton = new JButton("Завантажити вибрані аудіозаписи");
		downloadSelectedAudioButton.setBounds(10, 475, 240, 30);
		downloadSelectedAudioButton.setEnabled(false);

		downloadSelectedAudioButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
							
				downloading = new Thread(new Downloading(songsToDownload, false, "/method/audio.get", downloadVideoLink));
				downloading.start();
			}
		});
		
		downloadSelectedVideoButton = new JButton("Завантажити вибраний відеозапис");
		downloadSelectedVideoButton.setBounds(10, 475, 240, 30);
		downloadSelectedVideoButton.setEnabled(false);
		downloadSelectedVideoButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				downloading = new Thread(new Downloading(videosToDownload, false, "/method/video.get", downloadVideoLink));
				downloading.start();
			}
		});
		
		audioPanel.add(downloadSelectedAudioButton);
		videoPanel.add(downloadSelectedVideoButton);
		
		clearListAudioButton = new JButton("Очистити список вибраних аудіозаписів");
		clearListAudioButton.setBounds(470, 475, 270, 30);
		clearListAudioButton.setEnabled(false);
		clearListAudioButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				listToAudioDownload.removeAll();
				songsToDownload = new boolean[audiolist.length];
				downloadAudioListCounter = 1;
			}
		});
		
		clearListVideoButton = new JButton("Очистити список вибраних відеозаписів");
		clearListVideoButton.setBounds(470, 475, 270, 30);
		clearListVideoButton.setEnabled(false);
		clearListVideoButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				listToVideoDownload.removeAll();
				videosToDownload = new boolean[videolist.length];
				downloadVideoListCounter = 1;
			}
		});
		
		audioPanel.add(clearListAudioButton);
		videoPanel.add(clearListVideoButton);
		
		chooseDestinationAudioButton = new JButton("Оберіть куди завантажити аудіозапис (-и)");
		chooseDestinationAudioButton.setBounds(10, 550, 280, 30);
		chooseDestinationAudioButton.setEnabled(false);
		chooseDestinationAudioButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				chooseFolderDestination();
			}
		});
			
		audioPanel.add(chooseDestinationAudioButton);
	}
	
	public class Downloading implements Runnable {
		
		private boolean[] multimediaToDownload;
		boolean ifAll;
		String methodType;
		
		public Downloading (boolean[] multimedia, boolean ifAll, String methodType, JTextPane downloadVideoLink) {
			multimediaToDownload = multimedia.clone();
			this.ifAll = ifAll;
			this.methodType = methodType;
		}

	    public void run() {
	    	
			if(!ifAll) if(listToAudioDownload.getItemCount() == 0 && listToVideoDownload.getItemCount() == 0) return;
			
			if(!isAccesAllowed) {
				errorBox("Доступ до мультимедіа заборонено!", "Попередження");
				return;
			}
			
			if(!isFolderDestinationChoosed) chooseFolderDestination();
			if(!isFolderDestinationChoosed) return;
			
			downloadSelectedAudioButton.setEnabled(false);
			downloadAllAudioButton.setEnabled(false);
			downloadSelectedVideoButton.setEnabled(false);
			infoBox("Завантаження почалось","Завантаження");
			
			try {
				DownloadingProcess d = new DownloadingProcess (TOKEN_ACCESS, USER_ID, path, multimediaToDownload, downloading, methodType, downloadVideoLink);
       			d.pack();
				infoBox("Завантаження завершено","Завантаження");
			} catch (Exception e1) {
				errorBox("Щось пішло не так", "Невідома помилка");
			}
			finally {
				downloadSelectedAudioButton.setEnabled(true);
				downloadAllAudioButton.setEnabled(true);
				downloadSelectedVideoButton.setEnabled(true);
			}
	    }
	}
	
	private int getNumberOfDigits(int number) {
		
		int counter = 0;
		
		while(number > 0) {
			number /= 10;
			counter++;
		}
		
		return counter;
	}
	
	private void chooseFolderDestination() { // відображення розташування папки, в яку здійснюватиметься завантаження
		
		path = chooseFolder();
		
		if(path != null) {
			folderAudioDestinationLabel.setText(path);
			isFolderDestinationChoosed = true;
		}		
	}
	
	private String chooseFolder() { // вибір папки, в яку будуть завантажено файл (-и)
		
		File defaultDirectory = new File("C:\\Users\\ignat_000\\Downloads\\Polytech\\3 course\\Second semestr\\KursKS");
		JFileChooser dialog = new JFileChooser(defaultDirectory);
    	dialog.setDialogTitle("Оберіть папку");
    	dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	dialog.setMultiSelectionEnabled(false); 
    	dialog.setAcceptAllFileFilterUsed(false);
		
		if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return dialog.getSelectedFile().getAbsolutePath() + "\\";
		}
		
		return null;
	}
	
	private void errorBox(String infoMessage, String titleBar) { // для виведення повідомлення про помилку при завантаженні
		
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.ERROR_MESSAGE);
    }
    
    private void infoBox(String infoMessage, String titleBar) { // для виведення повідомлення про початок та кінець завантаження
    	
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    private String getTOKEN_ACCESS(String link) throws StringIndexOutOfBoundsException, NullPointerException { // отримуємо право доступу
    	
		StringBuilder access_token = new StringBuilder ();
		int start = link.indexOf("access_token");
		if(start == -1) throw new NullPointerException();
		else start += 13;
		
		for(; !("" + link.charAt(start)).equals("&"); start++) {
			access_token.append(link.charAt(start));
		}
		
		return access_token.toString();
	}
	
	private String getUSER_ID(String link) throws NullPointerException { // отримуємо ID користувача
		
		StringBuilder user_id = new StringBuilder ();
		int start = link.indexOf("user_id");
		if(start == -1) throw new NullPointerException();
		else start += 8;
		
		for(; start < link.length(); start++) {
			user_id.append(link.charAt(start));
		}
		
		return user_id.toString();
	}
	
	public static void main(String[] args) {
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client.protocol.ResponseProcessCookies", "fatal");
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
					ImageIcon icon = new ImageIcon("src/images/icon.png");
					frame.setIconImage(icon.getImage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}