package packageTrie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Trie {
	private final TrieNode root;

	public Trie() {
		root = new TrieNode();
	}

	//добавление узла с заданным ключем в дерево и изменение при этом top всех узлов на пути (если треубется)
	public void insert(int rank, String key) {
		TrieNode currentNode = root;
		for (char c : key.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			if ( child == null ) {
				currentNode = currentNode.addEdge(c);
			}
			else {
				//если в вершине величина списка топ меньше 10, то просто добавляем подсказку с запросом key
				if ( currentNode.top.keySet().size() < 10 ) {
					currentNode.top.put(rank, key);
				}
				//в противном случае - добавляем с заменой, если популярность нового запроса выше минимальной популярности
				//из запросов топ. Заменяем на менее популярный запрос.
				else {
	  				if (currentNode.getMinRank() < rank) {
					    currentNode.top.remove(currentNode.getMinRank());
					    currentNode.top.put(rank, key);
				    }
				}
				currentNode = child;
			}
		}
		currentNode.setKey(key);
	}

	//удаление узла с заданным ключем из дерева
	public void delete(String key) {
		TrieNode currentNode = root;
		for (char c : key.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			if ( child == null ) {
				return;
			}
			else {
				currentNode = child;
			}
		}
		currentNode.setKey(null);
	}

	//вернуть все ключи с заданным префиксом
	public List<String> search(String prefix) {
		TrieNode currentNode = root;
		for (char c : prefix.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			if ( child == null ) {
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
	
	//обходим дерево (начиная из currentNode) и из узлов формируем список
	private void preorderTraverse(TrieNode currentNode, List<String> results) {
		if ( currentNode == null ) { return; }
		if ( currentNode.getKey() != null ) {
			results.add(currentNode.getKey());
		}
		Iterator<TrieNode> children = currentNode.getChildren();
		if ( children != null ) {
			while ( children.hasNext()) {
				preorderTraverse(children.next(), results);
			}
		}
	}
	//функция подсказок - возвращает топ 10 элементов по заданному префиксу
	//возвращает список top, хранящийся в узле, отвечающем данному префиксу
	public List<String> hint(String prefix) {
		TrieNode currentNode = root;
		for (char c : prefix.toCharArray()) {
			TrieNode child = currentNode.traverse(c);
			if ( child == null ) {
				return Collections.emptyList();
			}
			else {
				currentNode = child;
			}
		}
		List<String> hintTop = new ArrayList<String>();
		Iterator<String> topIterator = currentNode.getTop();
		if ( topIterator != null ) {
			while ( topIterator.hasNext()) {
				hintTop.add(topIterator.next());
			}
		}
		return hintTop;
	}
}







