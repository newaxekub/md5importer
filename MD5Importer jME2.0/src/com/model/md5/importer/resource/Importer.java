package com.model.md5.importer.resource;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.jme.math.Vector3f;

/**
 * <code>Importer</code> defines the abstraction of an importer that provides
 * the common importing functionalities.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 11-18-2008 12:29 EST
 * @version Modified date: 11-18-2008 12:36 EST
 */
public class Importer {
	/**
	 * The <code>StreamTokenizer</code> instance.
	 */
	protected final StreamTokenizer reader;
	
	/**
	 * Constructor of <code>Importer</code>.
	 * @param reader The <code>StreamTokenizer</code> instance setup for reading file.
	 */
	protected Importer(StreamTokenizer reader) {
		this.reader = reader;
	}

	/**
	 * Read in a three-dimensional vector.
	 * @return The read in <code>Vector3f</code> instance.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	protected Vector3f readVector() throws IOException {
		float[] values = new float[3];
		for(int i = 0; i < 3; i++) {
			while(this.reader.ttype != StreamTokenizer.TT_NUMBER) {
				this.reader.nextToken();
			}
			values[i] = (float)this.reader.nval;
			this.reader.nextToken();
		}
		return new Vector3f(values[0], values[1], values[2]);
	}
}
