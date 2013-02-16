package packageTrie;
import java.util.*;
import java.io.*;

public class TrieTry {
	public static void main(String[] args) throws IOException {
		Trie trie = new Trie();
		read("/Users/roman/Documents/Broccoli/Doc/Yandex/homework/HintAtPrefix/kz_top.txt", trie);
		System.out.println("Запросы с префиксом \"ю\" " + trie.search("ю"));
	}

	//чтение входных данных по строкам и занесение их в Trie
	public static void read(String fileName, Trie trie) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String s;
		int rank;
		StringBuilder sb = new StringBuilder();

		while( (s = in.readLine()) != null) {
			//считываем ранк (популярность) запроса
			rank = readRank(s);
			//сохраняем верный вариант запроса (без опечаток который)
			s = readString(s);
			//добавляем запрос и его ранк (популярность)
			trie.insert(rank, s);
			sb.append(s + "\n");
		}
		in.close();
	}

	//возвращает правильный варинат запроса (без опечатки). Нужно доработать - обработать возможное исключение (если считываемая строка неправильного формата)
	public static String readString(String inputString) /*throws Exception*/ {
		String[] stringArray = inputString.split("\t");
		//if ( stringArray.length != 3 ) { throw new Exception("Wrong format"); }

		String resultString;
		//если второе поле пустое или содержит пробелы, считываем первое поле, иначе - считываем второе
		if ( stringArray[1].isEmpty() || stringArray[1].trim().isEmpty() ) { resultString = stringArray[0]; }
		else { resultString = stringArray[1]; }
		return resultString;
	}
	//считываем ранк запроса
	public static int readRank(String inputString) {
		String[] stringArray = inputString.split("\t");
		int rank = Integer.parseInt(stringArray[2]);
		return rank;
	}
}



