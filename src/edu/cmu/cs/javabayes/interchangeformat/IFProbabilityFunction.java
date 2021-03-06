/**
 * IFProbabilityFunction.java
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

import java.util.Enumeration;
import java.util.Vector;

/**
 * Auxiliary class that stores the temporary information about a
 * ProbabilityFunction, before it is possible to actually create a
 * ProbabilityFunction object (because not all variables may be read at this
 * point). The object stores the variable names in the function, the vector of
 * properties, and information that will be used to create the conditional
 * distribution tables (the default entry, the vector of entries, the table of
 * values; not all of them may be filled for a particular probability)
 * 
 * @author Fabio G. Cozman
 */
public class IFProbabilityFunction {
    String s_variables[];
    int conditional_index;
    Vector<String> properties;
    Vector<double[]> defaults;
    Vector<double[]> tables;
    Vector<IFProbabilityEntry> entries;

    public void set_variables(String vs[]) {
        this.s_variables = vs;
    }

    public void set_properties(Vector<String> p) {
        this.properties = p;
    }

    public void set_defaults(Vector<double[]> d) {
        this.defaults = d;
    }

    public void set_tables(Vector<double[]> t) {
        this.tables = t;
    }

    public void set_entries(Vector<IFProbabilityEntry> e) {
        this.entries = e;
    }

    public void set_conditional_index(int c) {
        this.conditional_index = c;
    }

    public String[] get_variables() {
        return this.s_variables;
    }

    public Vector<String> get_properties() {
        return (this.properties);
    } // Vector of String

    public Vector<double[]> get_defaults() {
        return (this.defaults);
    } // Vector of double[]

    public Vector<double[]> get_tables() {
        return (this.tables);
    } // Vector of double[]

    public Vector<IFProbabilityEntry> get_entries() {
        return (this.entries);
    } // Vector of IFProbabilityFunctionEntry

    public int get_conditional_index() {
        return (this.conditional_index);
    }

    /**
     * Method that inverts the tables in the ProbabilityFunction object;
     * necessary for formats that put the GIVEN variables as the lowest running
     * indexes in the tables. At this point it assumes that there is only one
     * FOR variable in the ProbabilityFunction object.
     * @param ifbn 
     */
    public void invert_tables(IFBayesNet ifbn) {
        IFProbabilityVariable pv;
        Vector<double[]> new_tables;
        Enumeration<double[]> e;
        Enumeration<IFProbabilityVariable> ee;
        String running_name;
        double t[], new_table[];
        int i, j;
        int size_of_first = 0, size_of_others = 1;

        if (this.s_variables.length > 1) { // No need to do anything if only one
                                      // variable.
            // Go through all the tables.
            new_tables = new Vector<>(); // Initialize a Vector for the
                                                 // new
            // tables.
            for (e = this.tables.elements(); e.hasMoreElements();) {
                size_of_first = 0;
                size_of_others = 1;
                t = (double[]) (e.nextElement()); // Get the table.
                // Now get the first variable.
                for (ee = ifbn.pvs.elements(); ee.hasMoreElements();) {
                    pv = (IFProbabilityVariable) (ee.nextElement());
                    running_name = pv.get_name();
                    if (running_name.equals(this.s_variables[0])) { // Found the
                                                               // first
                                                               // variable.
                        size_of_first = pv.get_values().length; // Obtain its
                                                                // size.
                        break; // Get out of loop through variables.
                    }
                }
                // Get the size of all other variables;
                for (j = 1; j < this.s_variables.length; j++) {
                    for (ee = ifbn.pvs.elements(); ee.hasMoreElements();) {
                        pv = (IFProbabilityVariable) (ee.nextElement());
                        running_name = pv.get_name();
                        if (running_name.equals(this.s_variables[j])) { // Found the
                                                                   // variable.
                            size_of_others *= pv.get_values().length;
                            break; // Get out of loop through variables.
                        }
                    }
                }
                // Build a new table.
                new_table = new double[t.length];
                for (i = 0; i < size_of_first; i++)
                    for (j = 0; j < size_of_others; j++)
                        new_table[i * size_of_others + j] = t[j * size_of_first
                                + i];
                // Insert the new table in the Vector new_tables.
                new_tables.addElement(new_table);
            }
            // Now attach the new Vector.
            this.tables = new_tables;
        }
    }
}
