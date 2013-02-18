package packageTrie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.io.*;

import java.util.*;


/**
 * Trie
 */
public class Trie {
	private final TrieNode root;
	public Trie() {
		root = new TrieNode();
	}

	/**  
	 * Добавление нового узла в Trie
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
			    /* в каждой вершине заполняем множество top десятью
				 * самыми популярными запросами по поддереву
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
	 * Поиск узла с заданным ключем в Trie
	 *
	 * @param key
	 *            запрос
	 *
	 * @return узел
	 */
	private TrieNode searchNode(String key) {
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
	 * Удаление узла из Trie с заданным ключем 
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
	 * Поиск всех узлов в Trie с ключем с заданным префиксом
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
	private void preorderTraverse(TrieNode currentNode, List<String> results) {
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
	 * Функция подсказок. Возвращает 10 подсказок по префиксу
	 *
	 * @param prefix
	 *              префикс
	 *
	 * @return список подсказок
	 *
	 */
	public List<String> hint(String prefix) {
		TrieNode currentNode = root;
		for (char c : prefix.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			if ( child == null ) {
				currentNode = near(prefix , 1);
				break;
				//return Collections.emptyList();
			}
			else {
				currentNode = child;
			}
		}

		List<String> hintTop = new ArrayList<String>();
		Iterator<String> topIterator = currentNode.getTop();
		if (topIterator != null) {
			while ( topIterator.hasNext()) {
				hintTop.add(topIterator.next());
			}
		}
		return hintTop;
	}

	/** 
	 * Создание Trie по данным из файла
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
	 * Выбирает из строки вариант запроса без опечаток
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
	 * Сохраняет ранк запроса
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
	 * Поиск узла с префиксом в Trie, который (префикс) отличается от данного не более, чем на
	 * заданную величину и имеет список топ с наибольшим рейтингом
	 *
	 * @param prefix
	 *              заданный префикс
	 * @param max
	 *           максимальное расстояние
	 *
	 * @return узел с "лучшим" префиксом (и близким к нашему и популярным)
	 */
	public TrieNode near(String prefix, int max) {
	   /* Сперва создам список всех узлов в Trie с префиксами длины prefix.length() - будем искать среди них
		* они являются детьми root в prefix.lenght()-ом поколении
		* для этого использую обход в ширину Trie до нужного места
		*/
		int counter = prefix.length();
		TrieNode currentNode = root;
		Queue<TrieNode> queue = new LinkedList<TrieNode>();
		queue.offer(currentNode);

		do {
			currentNode = queue.poll();
		    Iterator<TrieNode> children = currentNode.getChildren();
		    if (children != null) {
			    while (children.hasNext()) {
			    	queue.offer(children.next());
			    }
			}
		} while (queue.peek().getKey().length() < counter);

	   /* удалим слишком далекие префиксы, это те,
		* которые с точки зрения нашей метрики дальше от данного чем max
		*/
		DamerauLevensteinMetric metric = new DamerauLevensteinMetric();
		Iterator<TrieNode> iter1 = queue.iterator();
		while (iter1.hasNext()) {
			if (metric.getDistance(prefix, iter1.next().getKey(), 10) > max) { iter1.remove(); }
		}

		//найдем самый популяный префикс из перечисленных (с наибольшим ранком), остальные удалим
		int maxRank = 0;
		TrieNode node;
		TrieNode bestNode = null;
		Iterator<TrieNode> iterator = queue.iterator();
		while (iterator.hasNext()) {
			node = iterator.next();
			if (maxRank < node.getMinRank()) {
				maxRank = node.getMinRank();
				bestNode = node;
			}
			else {
				iterator.remove();
			}
		}

		//возвращаем узел с "лучшим" префиксом
		return bestNode;
	}
}


















