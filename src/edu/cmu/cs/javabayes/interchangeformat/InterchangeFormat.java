/**
 * InterchangeFormat.java
 * @author Fabio G. Cozman 
 * Copyright 1996 - 1999, Fabio G. Cozman,
 *          Carnergie Mellon University, Universidade de Sao Paulo
 * fgcozman@usp.br, http://www.cs.cmu.edu/~fgcozman/home.html
 *
 * The JavaBayes distribution is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation (either
 * version 2 of the License or, at your option, any later version), 
 * provided that this notice and the name of the author appear in all 
 * copies. Upon request to the author, some of the packages in the 
 * JavaBayes distribution can be licensed under the GNU Lesser General
 * Public License as published by the Free Software Foundation (either
 * version 2 of the License, or (at your option) any later version).
 * If you're using the software, please notify fgcozman@usp.br so
 * that you can receive updates and patches. JavaBayes is distributed
 * "as is", in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with the JavaBayes distribution. If not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package edu.cmu.cs.javabayes.interchangeformat;

import java.io.BufferedInputStream;
import java.io.InputStream;

import edu.cmu.cs.javabayes.parsers.BIFv01.BIFv01;
import edu.cmu.cs.javabayes.parsers.BIFv015.BIFv015;
import edu.cmu.cs.javabayes.parsers.XMLBIFv02.XMLBIFv02;
import edu.cmu.cs.javabayes.parsers.XMLBIFv03.XMLBIFv03;

public class InterchangeFormat {
	InputStream istream;
	XMLBIFv03 xml_bif03;
	XMLBIFv02 xml_bif02;
	BIFv015 bif015;
	BIFv01 bif01;

	// Size of the buffer for reading and resetting streams
	private final static int MARK_READ_LIMIT = 10000;

	public InterchangeFormat() {
	}

	public InterchangeFormat(InputStream is) {
		set_stream(is);
	}

	public void set_stream(InputStream is) {
		this.istream = new BufferedInputStream(is);
	}

	public void CompilationUnit() throws IFException {
		StringBuffer error_messages = new StringBuffer("Error messages\n");
		this.xml_bif03 = null;
		this.xml_bif02 = null;
		this.bif015 = null;
		this.bif01 = null;

		if (this.istream.markSupported()) {
			this.istream.mark(MARK_READ_LIMIT);
		} else
			error_messages.append("\nNo support for reset operation.");

		this.xml_bif03 = new XMLBIFv03(this.istream);
		try {
			this.xml_bif03.CompilationUnit();
			this.xml_bif03.invert_probability_tables();
		} catch (Throwable e4) { // Catch anything!
			error_messages.append(e4);
			try {
				this.istream.reset();
			} catch (Exception e) {
				error_messages.append("\n\nReset not allowed!");
			}
			error_messages.append("Input stream reset!\n");
			// Note that the following lines are within an enclosing catch
			// block.
			this.xml_bif02 = new edu.cmu.cs.javabayes.parsers.XMLBIFv02.XMLBIFv02(
					this.istream);
			try {
				this.xml_bif02.CompilationUnit();
			} catch (Throwable e3) { // Catch anything!
				error_messages.append(e3);
				try {
					this.istream.reset();
				} catch (Exception e) {
					error_messages.append("\n\nReset not allowed!");
				}
				error_messages.append("Input stream reset!\n");
				// Note that the following lines are within an enclosing catch
				// block.
				this.bif015 = new edu.cmu.cs.javabayes.parsers.BIFv015.BIFv015(
						this.istream);
				try {
					this.bif015.CompilationUnit();
				} catch (Throwable e2) { // Catch anything!
					error_messages.append(e2);
					try {
						this.istream.reset();
					} catch (Exception e) {
						error_messages.append("\n\nReset not allowed!");
					}
					error_messages.append("Input stream reset!\n");
					// Note that the following lines are within an enclosing
					// catch block.
					this.bif01 = new edu.cmu.cs.javabayes.parsers.BIFv01.BIFv01(
							this.istream);
					try {
						this.bif01.CompilationUnit();
					} catch (Throwable e1) { // Catch anything!
						error_messages.append(e1);
						throw new IFException(new String(error_messages));
					} // End bif01
				} // End bif015
			} // End xml_bif02
		} // End xml_bif03
	}

	public IFBayesNet get_ifbn() {
		IFBayesNet ifbn = null;

		if (this.xml_bif03 != null)
			ifbn = this.xml_bif03.get_ifbn();
		if (ifbn != null)
			return (ifbn);
		else {
			// Note that the following lines are inside an else.
			if (this.xml_bif02 != null)
				ifbn = this.xml_bif02.get_ifbn();
			if (ifbn != null)
				return (ifbn);
			else {
				// Note that the following lines are inside an else.
				if (this.bif015 != null)
					ifbn = this.bif015.get_ifbn();
				if (ifbn != null)
					return (ifbn);
				else {
					// Note that the following lines are inside an else.
					if (this.bif01 != null)
						ifbn = this.bif01.get_ifbn();
					if (ifbn != null)
						return (ifbn);
				} // End of bif01
			} // End of bif015
		} // End of xml_bif02
		return (ifbn);
	}
}
