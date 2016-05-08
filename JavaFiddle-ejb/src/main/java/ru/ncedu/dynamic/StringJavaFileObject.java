package ru.ncedu.dynamic;

import java.net.*;
import javax.tools.*;

/* 1 */public class StringJavaFileObject extends SimpleJavaFileObject {
	private CharSequence source;
        private String simpleName;

	/* 2 */public StringJavaFileObject(String name, CharSequence source) {
                
		/* 3 */super(URI.create("string:///" + name.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.source = source;
                simpleName = name;
	}

	@Override
	/* 4 */public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return this.source;
	}
        
        public String getSimpleName(){
            return simpleName;
        }
}