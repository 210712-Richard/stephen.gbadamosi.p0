package com.revature.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataSerializer<S> {
	
	// Generic Type - Replace all instances of an object type with a variable type T

	public List<S> readObjectsFromFile(String filename) {
		List<S> objects = null;
		try(ObjectInputStream o = new ObjectInputStream(new FileInputStream(filename));){
			objects = (ArrayList<S>) o.readObject();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objects;
	}

	public void writeObjectsToFile(List<S> objects, String filename) {
		try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(filename));){
			o.writeObject(objects);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
