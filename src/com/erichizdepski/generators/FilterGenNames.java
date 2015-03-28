package com.erichizdepski.generators;

import java.io.File;
import java.io.FilenameFilter;

public class FilterGenNames implements FilenameFilter
	{

		@Override
		public boolean accept(File directory, String name) {
			// TODO Auto-generated method stub
			
			return name.startsWith("Gen");
		}
		
	}

