package packageTrie;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Queue;


/**
 * Trie
 * @author roman khalkechev
 */
public class Trie {
	private final TrieNode root;
	public Trie() {
		root = new TrieNode();
	}

	/**  
	 * Добавление нового узла в Trie.
	 *
	 * @param rank
	 *            популярность запроса
	 * @param key
	 *            запрос
	 */
	public void insert(int rank, String key) {
		TrieNode currentNode = root;
		TrieNode prevNode;

		for (char c : key.toCharArray()) {
			TrieNode child = currentNode.traverse(c);

			if (child == null) {
				prevNode = currentNode;
				currentNode = currentNode.addEdge(c);
				if (prevNode != root) { currentNode.setKey(prevNode.getKey() + c); }
				else { currentNode.setKey(String.valueOf(c)); }
			}
			else {
			    /* В каждой вершине заполняем множество top десятью
				 * самыми популярными запросами по поддереву.
				 */
				if (currentNode.top.keySet().size() < 10) {
					currentNode.top.put(rank, key);
				}
				else {
	  				if (currentNode.getMinRank() < rank) {
					    currentNode.top.remove(currentNode.getMinRank());
					    currentNode.top.put(rank, key);
				    }
				}
				currentNode = child;
			}
		}
	}

	/**  
	 * Поиск узла с заданным ключем в Trie.
	 *
	 * @param key
	 *            запрос
	 *
	 * @return узел
	 */
	public TrieNode searchNode(String key) {
		TrieNode currentNode = root;
		for (char c : key.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			if (child == null) {
				return null;
			}
			else {
				currentNode = child;
			}
		}
		return currentNode;
	}

	/** 
	 * Удаление узла из Trie с заданным ключем.
	 *
	 * @param key
	 *            запрос
	 */
	public void delete(String key) {
		TrieNode currentNode = root;
		TrieNode prevNode = root;
		char lastChar = key.toCharArray()[key.length() - 1];

		for (char c : key.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			if (child == null) {
				return;
			}
			else {
				prevNode = currentNode;
				currentNode = child;
			}
		}
		prevNode.deleteEdge(lastChar);
	}

	/** 
	 * Поиск всех узлов в Trie с ключем с заданным префиксом.
	 *
	 * @param prefix
	 *              префикс
	 *
	 * @return список ключей из узлов
	 */
	public List<String> search(String prefix) {
		TrieNode currentNode = root;
		for (char c : prefix.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			if (child == null) {
				return Collections.emptyList();
			}
			else {
				currentNode = child;
			}
		}
		List<String> matches = new ArrayList<String>();
		preorderTraverse(currentNode, matches);
		return matches;
	}

	/** 
	 * Обход Trie начиная с заданного узла.
	 *
	 * @param currentNode
	 *                    стартовый узел
	 *
	 * @param results
	 *                список узлов, в который сохраняются узлы, которые обошли
	 */
	public void preorderTraverse(TrieNode currentNode, List<String> results) {
		if (currentNode == null) { return; }
		if (currentNode.getKey() != null) {
			results.add(currentNode.getKey());
		}
		Iterator<TrieNode> children = currentNode.getChildren();
		if (children != null) {
			while (children.hasNext()) {
				preorderTraverse(children.next(), results);
			}
		}
	}

	/** 
	 * Функция подсказок. Возвращает подсказоки по префиксу.
	 *
	 * @param prefix
	 *              префикс
	 *
	 * @return список подсказок
	 *
	 */
	public List<String> hint(String prefix) {
		//максимальное допустимое количество опечаток
		int maxMisprint = 0;
		//список подсказок
		List<String> hintTop = new ArrayList<String>();
		//была ли смена раскладки клавиатуры?
		boolean isKeyboardChange = false;
		TrieNode currentNode = root;
		for (char c : prefix.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			//нет подсказок и раскладку еще не меняли? попробуем сменить раскладку клавиатуры и заново поискать
			if (child == null && !isKeyboardChange) {
				String myPrefix = this.keyboardChange(prefix);
				isKeyboardChange = true;
				if (myPrefix != null) {
     				TrieNode myNode = root;
	    			for (char ch : myPrefix.toCharArray()) {
		    			TrieNode myChild = myNode.traverse(ch);
			    		if (myChild == null) { break; }
	    				else { myNode = myChild; }
		    		}
			    	if (myNode != null){
				    	Iterator<String> myTopIterator = myNode.getTop();
					    if (myTopIterator != null) {
			                while ( myTopIterator.hasNext()) {
			                    hintTop.add(myTopIterator.next());
		                    }
	                    }
	                    return hintTop;
				    }
			    }
			}
			//смена раскладки не помогла - вероятно в префиксе опечатка
			if ( child == null && isKeyboardChange) {
				//ищем близкие префиксы и выводим самый популярный
				//если префикс короткий (<= 5) - допускаем 1 опечатку
				if (prefix.length() <= 5) {
					maxMisprint = 1;
				}
				//если префикс длины > 5, то допускается 2 опечатки
				if (prefix.length() > 5) {
					maxMisprint = 2;
				}
				//возвращаем наиболее вероятные подсказки
				return near(prefix, maxMisprint);
			}
			else {
				currentNode = child;
			}
		}

		if (currentNode == null) { return Collections.emptyList();}
		Iterator<String> topIterator = currentNode.getTop();
		if (topIterator != null) {
			while ( topIterator.hasNext()) {
				hintTop.add(topIterator.next());
			}
		}
		return hintTop;
	}

	/** 
	 * Создание Trie по данным из файла.
	 *
	 * @param fileName
	 *                имя файла
	 *
	 */
	public void createTrie(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String s;
		int rank;

		while((s = in.readLine()) != null) {
			//считываем ранк (популярность) запроса
			rank = readRank(s);
			//сохраняем верный вариант запроса (без опечаток который)
			s = readString(s);
			//добавляем запрос и его ранк (популярность)
			this.insert(rank, s);
		}
		in.close();
	}

	/** 
	 * Сохраняет ранк запроса из строки.
	 *
	 * @param inputString
	 *                   строка
	 *
	 * @return ранк запроса
	 */
	private int readRank(String inputString) {
		String[] stringArray = inputString.split("\t");
		int rank = Integer.parseInt(stringArray[2]);
		return rank;
	}
	public TrieNode getRoot() {
		return root;
	}

	/** 
	 * Выбирает из строки вариант запроса без опечаток.
	 *
	 * @param inputString
	 *                   строка
	 *
     * @return запрос без опечаток
	 */
	private String readString(String inputString) /*throws Exception*/ {
		String[] stringArray = inputString.split("\t");
		//if ( stringArray.length != 3 ) { throw new Exception("Wrong format"); }
		String resultString;
		//если второе поле пустое или содержит пробелы, считываем первое поле, иначе - считываем второе
		if ( stringArray[1].isEmpty() || stringArray[1].trim().isEmpty() ) { resultString = stringArray[0]; }
		else { resultString = stringArray[1]; }
		return resultString;
	}
	
   	/** 
   	 * Возвращает наиболее вероятные подсказки для префикса, в котором вероятно сделана опечатка.
	 *
	 * @param prefix
	 *              заданный префикс
	 * @param max
	 *           максимальное расстояние
	 *
	 * @return список подсказок
	 */
	public List<String> near(String prefix, int max) {
	   /* Сперва создам список(очередь) всех узлов в Trie с префиксами длины prefix.length() и расстоянием до заданного prefix
		* не более чем max.
		* Для этого использую обход в ширину Trie до нужного места и функцию метрики.
		*/
		DamerauLevensteinMetric metric = new DamerauLevensteinMetric();
		int counter = prefix.length();
		TrieNode currentNode = root;
		Queue<TrieNode> queue = new LinkedList<TrieNode>();
		queue.offer(currentNode);
		TrieNode myNode = new TrieNode();

		do {
			currentNode = queue.poll();
		    Iterator<TrieNode> children = currentNode.getChildren();
		    if (children != null) {
			    while (children.hasNext()) {
			    	myNode = children.next();
			       /* Вношу в очередь только те узлы Trie, ключи которых либо "близки" (в смысле метрики) от префикса,
			    	* либо длина ключей которых меньше длины префикса минус max (допустимое число опечаток).
			    	*/
			    	if (myNode.getKey().length() <= counter - max || metric.getDistance(prefix, myNode.getKey(), max + 1) <= max) {
			    		queue.offer(myNode);
			    	}
			    }
			}
		} while (!queue.isEmpty() && (queue.peek().getKey().length() < counter));

		if (queue.isEmpty()) { return null;}
		//выберем три самых популярных
		int firstRank = 0;
		int secondRank = 0;
		int thirdRank = 0;
		TrieNode node;
		TrieNode firstNode = null;
		TrieNode secondNode = null;
		TrieNode thirdNode = null;
		
		Iterator<TrieNode> iterator = queue.iterator();
		while (iterator.hasNext()) {
			node = iterator.next();
			if (firstRank < node.getMinRank()) {
				thirdRank = secondRank;
				secondRank = firstRank;
				firstRank = node.getMinRank();

				thirdNode = secondNode;
				secondNode = firstNode;
				firstNode = node;
			}
		}

		//соберу лучшие подсказки из этих трех узлов в results: 5 подсказок из firstNode и по 3 из secondNode и thirdNode
		List<String> results = new ArrayList<String>();
		int count = 5;
		if (firstNode != null) {
			Iterator<String> firstIter = firstNode.getTop();
			while (firstIter.hasNext() && count > 0) {
				results.add(firstIter.next());
				count--;
			}
		}
		count = 3;
		if (secondNode != null) {
			Iterator<String> secondIter = secondNode.getTop();
			while (secondIter.hasNext() && count > 0) {
				results.add(secondIter.next());
				count--;
			}
		}
		count = 3;
		if (thirdNode != null) {
			Iterator<String> thirdIter = thirdNode.getTop();
			while (thirdIter.hasNext() && count > 0) {
				results.add(thirdIter.next());
				count--;
			}
		}
		//верну список подсказок
		return results;
	}

	/** 
	 * Меняет раскладку клавиатуры для слова.
	 *
	 * @param prefix
	 *              строка
	 *
     * @return строка в другой раскладке
	 */
	public String keyboardChange(String prefix) {
		Map<Character, Character> dictionary = new HashMap<Character, Character>();
		dictionary.put('q', 'й');
		dictionary.put('w', 'ц');
		dictionary.put('e', 'у');
		dictionary.put('r', 'к');
		dictionary.put('t', 'е');
		dictionary.put('y', 'н');
		dictionary.put('u', 'г');
		dictionary.put('i', 'ш');
		dictionary.put('o', 'щ');
		dictionary.put('p', 'з');
		dictionary.put('[', 'х');
		dictionary.put(']', 'ъ');
		dictionary.put('a', 'ф');
		dictionary.put('s', 'ы');
		dictionary.put('d', 'в');
		dictionary.put('f', 'а');
		dictionary.put('g', 'п');
		dictionary.put('h', 'р');
		dictionary.put('j', 'о');
		dictionary.put('k', 'л');
		dictionary.put('l', 'д');
		dictionary.put(';', 'ж');
		dictionary.put('\'', 'э');
		dictionary.put('z', 'я');
		dictionary.put('x', 'ч');
		dictionary.put('c', 'с');
		dictionary.put('v', 'м');
		dictionary.put('b', 'и');
		dictionary.put('n', 'т');
		dictionary.put('m', 'ь');
		dictionary.put(',', 'б');
		dictionary.put('.', 'ю');
		dictionary.put(' ', ' ');
		dictionary.put('0', '0');
		dictionary.put('1', '1');
		dictionary.put('2', '2');
		dictionary.put('3', '3');
		dictionary.put('4', '4');
		dictionary.put('5', '5');
		dictionary.put('6', '6');
		dictionary.put('7', '7');
		dictionary.put('8', '8');
		dictionary.put('9', '9');

		StringBuilder prefixChange = new StringBuilder();
		for (char c : prefix.toCharArray()) {
			if(dictionary.get(c) == null) { return null; }
			prefixChange.append(dictionary.get(c));
		}
		return prefixChange.toString();
	}
}

