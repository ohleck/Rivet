// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// Rivet Copyright (C) 2011 Ian Wraith
// This program comes with ABSOLUTELY NO WARRANTY

package org.e2k;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class MFSK {
	
	public final int MINI_FFT_SIZE=8;
	public final int SHORT_FFT_SIZE=128;
	public final int MID_FFT_SIZE=512;
	public final int LONG_FFT_SIZE=1024;
	private DoubleFFT_1D long_fft=new DoubleFFT_1D(LONG_FFT_SIZE);
	private DoubleFFT_1D mid_fft=new DoubleFFT_1D(MID_FFT_SIZE);
	private DoubleFFT_1D mini_fft=new DoubleFFT_1D(MINI_FFT_SIZE);
	private DoubleFFT_1D short_fft=new DoubleFFT_1D(SHORT_FFT_SIZE);
	private double totalEnergy;
	
	// Return the number of samples per baud
	public double samplesPerSymbol (double dbaud,double sampleFreq)	{
		return (sampleFreq/dbaud);
	}
	
	// Test for a specific tone
	public boolean toneTest (int freq,int tone,int errorAllow)	{
	    if ((freq>(tone-errorAllow))&&(freq<(tone+errorAllow))) return true;
	     else return false;
	  }
	
	// Find the bin containing the hight value from an array of doubles
	private int findHighBin(double[]x)	{
		int a,highBin=-1;
		double highVal=-1;
		for (a=0;a<x.length;a++)	{
			if (x[a]>highVal)	{
				highVal=x[a];
				highBin=a;
			}
		}
		// Return the highest bin position
		return highBin+1;
	}
		
	// Given the real data in a double array return the largest frequency component
	private int getFFTFreq (double[]x,double sampleFreq,int correctionFactor)	{
		int bin=findHighBin(x);
		double len=x.length*2;
		double ret=((sampleFreq/len)*bin)-correctionFactor;
		return (int)ret;
	}
	
	// We have a problem since FFT sizes must be to a power of 2 but samples per symbol can be any value
	// So instead I am doing a FFT in the middle of the symbol
	public int symbolFreq (CircularDataBuffer circBuf,WaveData waveData,int start,double samplePerSymbol)	{
		// There must be at least LONG_FFT_SIZE samples Per Symbol
		if (samplePerSymbol<LONG_FFT_SIZE) return -1;
		int fftStart=start+(((int)samplePerSymbol-LONG_FFT_SIZE)/2);
		double freq=doFFT(circBuf,waveData,fftStart,LONG_FFT_SIZE);
		return (int)freq;
	}
	
	public int doFFT (CircularDataBuffer circBuf,WaveData waveData,int start,int length)	{
		// Get the data from the circular buffer
	    double datar[]=circBuf.extractDataDouble(start,length);
		long_fft.realForward(datar);
		double spec[]=getSpectrum(datar);
		int freq=getFFTFreq (spec,waveData.sampleRate,waveData.longCorrectionFactor);  
		return freq;
	}
	
	public int doShortFFT (CircularDataBuffer circBuf,WaveData waveData,int start)	{
		// Get the data from the circular buffer
	    double datar[]=circBuf.extractDataDouble(start,SHORT_FFT_SIZE);
		short_fft.realForward(datar);
		double spec[]=getSpectrum(datar);
		int freq=getFFTFreq (spec,waveData.sampleRate,waveData.shortCorrectionFactor);  
		return freq;
	}
	
	public int doMidFFT (CircularDataBuffer circBuf,WaveData waveData,int start)	{
		// Get the data from the circular buffer
	    double datar[]=circBuf.extractDataDouble(start,MID_FFT_SIZE);
		mid_fft.realForward(datar);
		double spec[]=getSpectrum(datar);
		int freq=getFFTFreq (spec,waveData.sampleRate,waveData.shortCorrectionFactor);  
		return freq;
	}
	
	public int doMiniFFT (CircularDataBuffer circBuf,WaveData waveData,int start)	{
		// Get the data from the circular buffer
	    double datar[]=circBuf.extractDataDouble(start,MINI_FFT_SIZE);
		mini_fft.realForward(datar);
		double spec[]=getSpectrum(datar);
		int freq=getFFTFreq (spec,waveData.sampleRate,waveData.shortCorrectionFactor);  
		return freq;
	}
	
	// Combine the complex data returned by the JTransform FFT routine to provide
	// a power spectrum
	private double[] getSpectrum (double[]data)	{
		double spectrum[]=new double[data.length/2];
		// Clear the total energy sum
		totalEnergy=0.0;
		int a,count=0;
		for (a=2;a<data.length;a=a+2)	{
			spectrum[count]=Math.sqrt(Math.pow(data[a],2.0)+Math.pow(data[a+1],2.0));
			// Add this to the total energy sum
			totalEnergy=totalEnergy+spectrum[count];
			count++;
		}
		return spectrum;
	}
	
	// Return the total energy sum
	public double getTotalEnergy ()	{
		return this.totalEnergy;
	}
	

}