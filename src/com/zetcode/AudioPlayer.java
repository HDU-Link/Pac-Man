package com.zetcode;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	public void Music(String filePath) {
		try {
			AudioInputStream Stream = AudioSystem.getAudioInputStream(new File(filePath));
			Clip clip = AudioSystem.getClip();
			clip.open(Stream);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			System.err.println("警告: 不支持的音频文件格式。");
		} catch (IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
