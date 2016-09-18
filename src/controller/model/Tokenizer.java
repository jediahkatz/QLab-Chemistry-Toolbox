package controller.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Ack to nayuki.io for the tokenizer and any method that takes a tokenizer as an argument
public class Tokenizer {
	private int i=0;
	private String str;
	
	Tokenizer(String str) {
		str.replace(" ", "");
		this.str = str;
	}
	
	int position() {
		return i;
	}
	
	String peek() {
		if(i==str.length()) {
			return null;
		}
		Matcher match = Pattern.compile("^([A-Za-z][a-z]*|[0-9]+|[+\\-^=()])").matcher(str.substring(i));
		if(match.find()) {
			return match.group();
		}
		System.out.println(str + " :: l" + str.substring(i));
		throw(new IllegalArgumentException("Illegal symbol"));
	}
	
	String take() {
		String result = this.peek();
		if(result == null) {
			throw(new StringIndexOutOfBoundsException());
		}
		i+=result.length();
		skipSpaces();
		return result;
	}
	
	void consume(String s) {
		if(!this.take().equals(s)) {
			throw(new NullPointerException("Token mismatch"));
		}
	}
	
	void skipSpaces() {
		Matcher spaces = Pattern.compile("^[ \t]*").matcher(str.substring(i));
		spaces.find();
		i += spaces.end();
	}
}
