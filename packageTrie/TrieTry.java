package packageTrie;
import java.util.*;
import java.io.*;

public class TrieTry {
	public static void main(String[] args) throws IOException {
		Trie trie = new Trie();
		//Считаем из файла и построим наше правильное Trie - без опечаток
		System.out.println("Создаем Trie из поисковых запросов.");
		trie.createTrie("/Users/roman/Documents/Broccoli/Doc/GitHub/homework/HintAtPrefix/packageTrie/kz_top.txt");

		//потестим подсказки :)
		Scanner scan = new Scanner(System.in);
		for (int i = 1; i < 10; i++) {
		    System.out.println("Тест №" + i + " Введите prefix и получите топ 10 подсказок.");
		    String prefix = scan.nextLine();

		    //ищем подсказки
		    long before = System.currentTimeMillis();
		    List<String> results = trie.hint(prefix);
		    long after = System.currentTimeMillis();
		    if (results != null) {
		        Iterator<String> iterator = results.iterator();
    		    while (iterator.hasNext()) {
	    	        System.out.println(iterator.next());
		        }
		        System.out.println("Время работы: " + (after - before));
		    }
	    }
	    
	}
}

