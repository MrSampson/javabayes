package edu.cmu.cs.javabayes.parsers.XMLBIFv02;

import java.util.Enumeration;
import java.util.Vector;

import edu.cmu.cs.javabayes.interchangeformat.IFBayesNet;
import edu.cmu.cs.javabayes.interchangeformat.IFProbabilityEntry;
import edu.cmu.cs.javabayes.interchangeformat.IFProbabilityFunction;
import edu.cmu.cs.javabayes.interchangeformat.IFProbabilityVariable;
import edu.cmu.cs.javabayes.interchangeformat.InterchangeFormat;

/**
 * Definition of the Interchange Format class and its variables. The IFBayesNet
 * ifbn contains the parsed Bayesian network.
 */

public class XMLBIFv02 extends InterchangeFormat implements XMLBIFv02Constants {
	IFBayesNet ifbn;

	@Override
	public IFBayesNet get_ifbn() {
		return (ifbn);
	}

	String pcdata() throws ParseException {
		StringBuffer p = new StringBuffer("");
		Token t;
		while (true) {
			t = getToken(1);
			if ((t.kind == 0) || (t.kind == SOT) || (t.kind == EOT))
				break;
			else {
				p.append(t.image);
				getNextToken();
			}
		}
		return (p.toString());
	}

	void glob_heading() throws ParseException {
		Token t;
		while (true) {
			t = getToken(1);
			if (t.kind == 0)
				break;
			else {
				if (t.kind == SOT) {
					getNextToken();
					t = getToken(1);
					if (t.kind == BIF) {
						getNextToken();
						t = getToken(1);
						if (t.kind == CT) {
							getNextToken();
							break;
						}
					} else {
						getNextToken();
					}
				} else {
					getNextToken();
				}
			}
			getNextToken();
		}
	}

	/**
	 * THE INTERCHANGE FORMAT GRAMMAR STARTS HERE.
	 */

	/**
	 * Basic parsing function. First looks for a Network Declaration, then looks
	 * for an arbitrary number of VariableDeclaration or ProbabilityDeclaration
	 * non-terminals. The objects are in the vectors ifbn.pvs and ifbn.upfs.
	 */
	@Override
	final public void CompilationUnit() throws ParseException {
		IFProbabilityVariable pv;
		IFProbabilityFunction upf;
		OpenTag();
		glob_heading();
		NetworkDeclaration();
		label_1: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case SOT:
				;
				break;
			default:
				jj_la1[0] = jj_gen;
				break label_1;
			}
			jj_consume_token(SOT);
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case VARIABLE:
				pv = VariableDeclaration();
				ifbn.add(pv);
				break;
			case PROBABILITY:
				upf = ProbabilityDeclaration();
				ifbn.add(upf);
				break;
			default:
				jj_la1[1] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
		}
		jj_consume_token(EOT);
		jj_consume_token(NETWORK);
		jj_consume_token(CT);
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case EOT:
			jj_consume_token(EOT);
			jj_consume_token(BIF);
			jj_consume_token(CT);
			break;
		case 0:
			jj_consume_token(0);
			break;
		default:
			jj_la1[2] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
	}

	final public void OpenTag() throws ParseException {
		jj_consume_token(OPENTAG);
	}

	/**
	 * Detect and initialize the network.
	 */
	final public void NetworkDeclaration() throws ParseException {
		String s, ss;
		Vector properties = new Vector();
		jj_consume_token(SOT);
		jj_consume_token(NETWORK);
		jj_consume_token(CT);
		jj_consume_token(SOT);
		jj_consume_token(NAME);
		s = getString();
		jj_consume_token(EOT);
		jj_consume_token(NAME);
		jj_consume_token(CT);
		label_2: while (true) {
			if (jj_2_1(2)) {
				;
			} else {
				break label_2;
			}
			ss = Property();
			properties.addElement(ss);
		}
		ifbn = new IFBayesNet(s, properties);
	}

	/**
	 * Detect a variable declaration.
	 */
	final public IFProbabilityVariable VariableDeclaration()
			throws ParseException {
		String s;
		IFProbabilityVariable pv;
		jj_consume_token(VARIABLE);
		jj_consume_token(CT);
		s = ProbabilityVariableName();
		ProbabilityVariableType();
		pv = VariableContent(s);
		jj_consume_token(EOT);
		jj_consume_token(VARIABLE);
		jj_consume_token(CT);

		return (pv);

	}

	final public String ProbabilityVariableName() throws ParseException {
		String s;
		jj_consume_token(SOT);
		jj_consume_token(NAME);
		s = getString();
		jj_consume_token(EOT);
		jj_consume_token(NAME);
		jj_consume_token(CT);

		return (s);

	}

	final public void ProbabilityVariableType() throws ParseException {
		String values[] = null;
		jj_consume_token(SOT);
		jj_consume_token(TYPE);
		jj_consume_token(CT);
		jj_consume_token(DISCRETE);
		jj_consume_token(EOT);
		jj_consume_token(TYPE);
		jj_consume_token(CT);
	}

	final public IFProbabilityVariable VariableContent(String name)
			throws ParseException {
		int i;
		String s, v, svalues[];
		Vector properties = new Vector();
		Vector values = new Vector();
		Enumeration e;
		IFProbabilityVariable pv = new IFProbabilityVariable();
		label_3: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case SOT:
				;
				break;
			default:
				jj_la1[3] = jj_gen;
				break label_3;
			}
			if (jj_2_2(2)) {
				s = Property();
				properties.addElement(s);
			} else {
				switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
				case SOT:
					v = VariableValue();
					values.addElement(v);
					break;
				default:
					jj_la1[4] = jj_gen;
					jj_consume_token(-1);
					throw new ParseException();
				}
			}
		}
		pv.set_name(name);
		pv.set_properties(properties);
		svalues = new String[values.size()];
		for (e = values.elements(), i = 0; e.hasMoreElements(); i++)
			svalues[i] = (String) (e.nextElement());
		pv.set_values(svalues);

		return (pv);

	}

	final public String VariableValue() throws ParseException {
		String s;
		jj_consume_token(SOT);
		jj_consume_token(VALUE);
		s = getString();
		jj_consume_token(EOT);
		jj_consume_token(VALUE);
		jj_consume_token(CT);

		return (s);

	}

	/**
	 * Detect a probability declaration.
	 */
	final public IFProbabilityFunction ProbabilityDeclaration()
			throws ParseException {
		String vs[];
		IFProbabilityFunction upf = new IFProbabilityFunction();
		jj_consume_token(PROBABILITY);
		jj_consume_token(CT);
		ProbabilityContent(upf);
		jj_consume_token(EOT);
		jj_consume_token(PROBABILITY);
		jj_consume_token(CT);

		return (upf);

	}

	final public void ProbabilityContent(IFProbabilityFunction upf)
			throws ParseException {
		int i, j;
		double def[] = null;
		double tab[] = null;
		String s, vs[];
		IFProbabilityEntry entry = null;
		Enumeration e;

		Vector fors = new Vector();
		Vector givens = new Vector();
		Vector properties = new Vector();
		Vector entries = new Vector();
		Vector defaults = new Vector();
		Vector tables = new Vector();
		label_4: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case SOT:
				;
				break;
			default:
				jj_la1[5] = jj_gen;
				break label_4;
			}
			jj_consume_token(SOT);
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case FOR:
				s = ProbabilityFor();
				fors.addElement(s);
				break;
			case GIVEN:
				s = ProbabilityGiven();
				givens.addElement(s);
				break;
			case SOT:
				s = Property();
				properties.addElement(s);
				break;
			case DEFAUL:
				def = ProbabilityDefault();
				defaults.addElement(def);
				break;
			case ENTRY:
				entry = ProbabilityEntry();
				entries.addElement(entry);
				break;
			case TABLE:
				tab = ProbabilityTable();
				tables.addElement(tab);
				break;
			default:
				jj_la1[6] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
		}
		upf.set_properties(properties);
		upf.set_defaults(defaults);
		upf.set_entries(entries);
		upf.set_tables(tables);
		upf.set_conditional_index(fors.size());
		vs = new String[fors.size() + givens.size()];
		for (e = fors.elements(), i = 0; e.hasMoreElements(); i++)
			vs[i] = (String) (e.nextElement());
		for (e = givens.elements(), j = i; e.hasMoreElements(); j++)
			vs[j] = (String) (e.nextElement());
		upf.set_variables(vs);
	}

	final public String ProbabilityFor() throws ParseException {
		String s;
		jj_consume_token(FOR);
		s = getString();
		jj_consume_token(EOT);
		jj_consume_token(FOR);
		jj_consume_token(CT);

		return (s);

	}

	final public String ProbabilityGiven() throws ParseException {
		String s;
		jj_consume_token(GIVEN);
		s = getString();
		jj_consume_token(EOT);
		jj_consume_token(GIVEN);
		jj_consume_token(CT);

		return (s);

	}

	final public IFProbabilityEntry ProbabilityEntry() throws ParseException {
		int i;
		Enumeration e;
		String variable_name, vs[];
		Vector v_list = new Vector();
		double d[];
		jj_consume_token(ENTRY);
		jj_consume_token(CT);
		label_5: while (true) {
			if (jj_2_3(2)) {
				;
			} else {
				break label_5;
			}
			jj_consume_token(SOT);
			jj_consume_token(VALUE);
			variable_name = getString();
			jj_consume_token(EOT);
			jj_consume_token(VALUE);
			jj_consume_token(CT);
			v_list.addElement(variable_name);
		}
		d = ProbabilityTable();
		jj_consume_token(EOT);
		jj_consume_token(ENTRY);
		jj_consume_token(CT);
		vs = new String[v_list.size()];
		for (e = v_list.elements(), i = 0; e.hasMoreElements(); i++)
			vs[i] = (String) (e.nextElement());

		return (new IFProbabilityEntry(vs, d));

	}

	final public double[] ProbabilityDefault() throws ParseException {
		double d[];
		jj_consume_token(DEFAUL);
		jj_consume_token(CT);
		d = FloatingPointList();
		jj_consume_token(EOT);
		jj_consume_token(DEFAUL);
		jj_consume_token(CT);

		return (d);

	}

	final public double[] ProbabilityTable() throws ParseException {
		double d[];
		jj_consume_token(TABLE);
		jj_consume_token(CT);
		d = FloatingPointList();
		jj_consume_token(EOT);
		jj_consume_token(TABLE);
		jj_consume_token(CT);

		return (d);

	}

	/**
	 * Some general purpose non-terminals.
	 */

	/**
	 * Pick a list of non-negative floating numbers.
	 */
	final public double[] FloatingPointList() throws ParseException {
		int i;
		Double d;
		double ds[];
		Vector d_list = new Vector();
		Enumeration e;
		d = FloatingPointNumber();
		d_list.addElement(d);
		label_6: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case NON_NEGATIVE_NUMBER:
				;
				break;
			default:
				jj_la1[7] = jj_gen;
				break label_6;
			}
			d = FloatingPointNumber();
			d_list.addElement(d);
		}
		ds = new double[d_list.size()];
		for (e = d_list.elements(), i = 0; e.hasMoreElements(); i++) {
			d = (Double) (e.nextElement());
			ds[i] = d.doubleValue();
		}
		{
			if (true)
				return (ds);
		}
		throw new Error("Missing return statement in function");
	}

	/**
	 * Pick a non-negative floating number; necessary to allow ignored
	 * characters and comments to exist in the middle of a FloatingPointList().
	 */
	final public Double FloatingPointNumber() throws ParseException {
		Token t;
		t = jj_consume_token(NON_NEGATIVE_NUMBER);
		{
			if (true)
				return (Double.valueOf(t.image));
		}
		throw new Error("Missing return statement in function");
	}

	/**
	 * Property definition.
	 */
	final public String Property() throws ParseException {
		String s;
		jj_consume_token(SOT);
		jj_consume_token(PROPERTY);
		s = getString();
		jj_consume_token(EOT);
		jj_consume_token(PROPERTY);
		jj_consume_token(CT);
		{
			if (true)
				return (s);
		}
		throw new Error("Missing return statement in function");
	}

	/**
	 * String.
	 */
	final public String getString() throws ParseException {
		jj_consume_token(CT);
		{
			if (true)
				return (pcdata());
		}
		throw new Error("Missing return statement in function");
	}

	final private boolean jj_2_1(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		boolean retval = !jj_3_1();
		jj_save(0, xla);
		return retval;
	}

	final private boolean jj_2_2(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		boolean retval = !jj_3_2();
		jj_save(1, xla);
		return retval;
	}

	final private boolean jj_2_3(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		boolean retval = !jj_3_3();
		jj_save(2, xla);
		return retval;
	}

	final private boolean jj_3_2() {
		if (jj_3R_7())
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			return false;
		return false;
	}

	final private boolean jj_3_3() {
		if (jj_scan_token(SOT))
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			return false;
		if (jj_scan_token(VALUE))
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			return false;
		return false;
	}

	final private boolean jj_3R_7() {
		if (jj_scan_token(SOT))
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			return false;
		if (jj_scan_token(PROPERTY))
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			return false;
		return false;
	}

	final private boolean jj_3_1() {
		if (jj_3R_7())
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			return false;
		return false;
	}

	public XMLBIFv02TokenManager token_source;
	ASCII_CharStream jj_input_stream;
	public Token token, jj_nt;
	private int jj_ntk;
	private Token jj_scanpos, jj_lastpos;
	private int jj_la;
	public boolean lookingAhead = false;
	private boolean jj_semLA;
	private int jj_gen;
	final private int[] jj_la1 = new int[8];
	final private int[] jj_la1_0 = { 0x8, 0x84000, 0x11, 0x8, 0x8, 0x8,
			0x10f08, 0x200000, };
	final private JJXMLBIFv02Calls[] jj_2_rtns = new JJXMLBIFv02Calls[3];
	private boolean jj_rescan = false;
	private int jj_gc = 0;

	public XMLBIFv02(java.io.InputStream stream) {
		jj_input_stream = new ASCII_CharStream(stream, 1, 1);
		token_source = new XMLBIFv02TokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJXMLBIFv02Calls();
	}

	public void ReInit(java.io.InputStream stream) {
		jj_input_stream.ReInit(stream, 1, 1);
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJXMLBIFv02Calls();
	}

	public XMLBIFv02(XMLBIFv02TokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJXMLBIFv02Calls();
	}

	public void ReInit(XMLBIFv02TokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJXMLBIFv02Calls();
	}

	final private Token jj_consume_token(int kind) throws ParseException {
		Token oldToken;
		if ((oldToken = token).next != null)
			token = token.next;
		else
			token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		if (token.kind == kind) {
			jj_gen++;
			if (++jj_gc > 100) {
				jj_gc = 0;
				for (int i = 0; i < jj_2_rtns.length; i++) {
					JJXMLBIFv02Calls c = jj_2_rtns[i];
					while (c != null) {
						if (c.gen < jj_gen)
							c.first = null;
						c = c.next;
					}
				}
			}
			return token;
		}
		token = oldToken;
		jj_kind = kind;
		throw generateParseException();
	}

	final private boolean jj_scan_token(int kind) {
		if (jj_scanpos == jj_lastpos) {
			jj_la--;
			if (jj_scanpos.next == null) {
				jj_lastpos = jj_scanpos = jj_scanpos.next = token_source
						.getNextToken();
			} else {
				jj_lastpos = jj_scanpos = jj_scanpos.next;
			}
		} else {
			jj_scanpos = jj_scanpos.next;
		}
		if (jj_rescan) {
			int i = 0;
			Token tok = token;
			while (tok != null && tok != jj_scanpos) {
				i++;
				tok = tok.next;
			}
			if (tok != null)
				jj_add_error_token(kind, i);
		}
		return (jj_scanpos.kind != kind);
	}

	final public Token getNextToken() {
		if (token.next != null)
			token = token.next;
		else
			token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		jj_gen++;
		return token;
	}

	final public Token getToken(int index) {
		Token t = lookingAhead ? jj_scanpos : token;
		for (int i = 0; i < index; i++) {
			if (t.next != null)
				t = t.next;
			else
				t = t.next = token_source.getNextToken();
		}
		return t;
	}

	final private int jj_ntk() {
		if ((jj_nt = token.next) == null)
			return (jj_ntk = (token.next = token_source.getNextToken()).kind);
		else
			return (jj_ntk = jj_nt.kind);
	}

	private java.util.Vector jj_expentries = new java.util.Vector();
	private int[] jj_expentry;
	private int jj_kind = -1;
	private int[] jj_lasttokens = new int[100];
	private int jj_endpos;

	private void jj_add_error_token(int kind, int pos) {
		if (pos >= 100)
			return;
		if (pos == jj_endpos + 1) {
			jj_lasttokens[jj_endpos++] = kind;
		} else if (jj_endpos != 0) {
			jj_expentry = new int[jj_endpos];
			for (int i = 0; i < jj_endpos; i++) {
				jj_expentry[i] = jj_lasttokens[i];
			}
			boolean exists = false;
			for (java.util.Enumeration e = jj_expentries.elements(); e
					.hasMoreElements();) {
				int[] oldentry = (int[]) (e.nextElement());
				if (oldentry.length == jj_expentry.length) {
					exists = true;
					for (int i = 0; i < jj_expentry.length; i++) {
						if (oldentry[i] != jj_expentry[i]) {
							exists = false;
							break;
						}
					}
					if (exists)
						break;
				}
			}
			if (!exists)
				jj_expentries.addElement(jj_expentry);
			if (pos != 0)
				jj_lasttokens[(jj_endpos = pos) - 1] = kind;
		}
	}

	final public ParseException generateParseException() {
		jj_expentries.removeAllElements();
		boolean[] la1tokens = new boolean[24];
		for (int i = 0; i < 24; i++) {
			la1tokens[i] = false;
		}
		if (jj_kind >= 0) {
			la1tokens[jj_kind] = true;
			jj_kind = -1;
		}
		for (int i = 0; i < 8; i++) {
			if (jj_la1[i] == jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & (1 << j)) != 0) {
						la1tokens[j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 24; i++) {
			if (la1tokens[i]) {
				jj_expentry = new int[1];
				jj_expentry[0] = i;
				jj_expentries.addElement(jj_expentry);
			}
		}
		jj_endpos = 0;
		jj_rescan_token();
		jj_add_error_token(0, 0);
		int[][] exptokseq = new int[jj_expentries.size()][];
		for (int i = 0; i < jj_expentries.size(); i++) {
			exptokseq[i] = (int[]) jj_expentries.elementAt(i);
		}
		return new ParseException(token, exptokseq, tokenImage);
	}

	final public void enable_tracing() {
	}

	final public void disable_tracing() {
	}

	final private void jj_rescan_token() {
		jj_rescan = true;
		for (int i = 0; i < 3; i++) {
			JJXMLBIFv02Calls p = jj_2_rtns[i];
			do {
				if (p.gen > jj_gen) {
					jj_la = p.arg;
					jj_lastpos = jj_scanpos = p.first;
					switch (i) {
					case 0:
						jj_3_1();
						break;
					case 1:
						jj_3_2();
						break;
					case 2:
						jj_3_3();
						break;
					}
				}
				p = p.next;
			} while (p != null);
		}
		jj_rescan = false;
	}

	final private void jj_save(int index, int xla) {
		JJXMLBIFv02Calls p = jj_2_rtns[index];
		while (p.gen > jj_gen) {
			if (p.next == null) {
				p = p.next = new JJXMLBIFv02Calls();
				break;
			}
			p = p.next;
		}
		p.gen = jj_gen + xla - jj_la;
		p.first = token;
		p.arg = xla;
	}

}

final class JJXMLBIFv02Calls {
	int gen;
	Token first;
	int arg;
	JJXMLBIFv02Calls next;
}
