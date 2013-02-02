package examples;

import java.util.concurrent.locks.ReadWriteLock;

public class CoarseHashSet<T> implements Set<T> {
	private ReadWriteLock entire; 

	/**
	 * Helper class - basically is a linked list of items that happen to map to
	 * the same hash code.
	 */
	
	private static class Bucket {
		/**
		 * The item stored at this entry. This is morally of type T, but Java
		 * generics do not play well with arrays, so we have to use Object
		 * instead.
		 */
		Object item;

		/**
		 * Next item in the list.
		 */
		Bucket next;

		/**
		 * Create a new bucket.
		 * 
		 * @param item item to be stored
		 * @param next next item in the list
		 */
		public Bucket(Object item, Bucket next) {
			this.item = item;
			this.next = next;
		}
	}

	/**
	 * Our array of items. Each location in the array stores a linked list items
	 * that hash to that locations.
	 */
	private Bucket[] table;

	/**
	 * Capacity of the array. Since we do not support resizing, this is a
	 * constant.
	 */
	private static final int CAPACITY = 1024;

	/**
	 * Create a new HashSet.
	 */
	public CoarseHashSet() {
		this.table = new Bucket[CAPACITY];
	}

	/**
	 * A helper method to see if an item is stored at a given bucket.
	 * 
	 * @param bucket bucket to be searched
	 * @param item item to be searched for
	 * @return true if the item is in the bucket
	 */
	private boolean contains(Bucket bucket, T item) {

			while (bucket != null) {
				if (item.equals(bucket.item)) {
					return true;

				}
				bucket = bucket.next;
			}
			return false;
		
	}

	/*
	 * (non-Javadoc)
	 * @see examples.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(T item) {
		// Java returns a negative number for the hash; this is just converting
		// the negative number to a location in the array.
		int hash = (item.hashCode() % CAPACITY + CAPACITY) % CAPACITY;
		entire.readLock().lock();
		try {
			Bucket bucket = table[hash];
			if (contains(bucket, item)) {
				return false;
			}
			table[hash] = new Bucket(item, bucket);
		} finally {
			entire.readLock().unlock();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see examples.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T item) {
		int hash = (item.hashCode() % CAPACITY + CAPACITY) % CAPACITY;
		entire.readLock().lock();
		try {
		Bucket bucket = table[hash];
		return contains(bucket, item);
		} finally {
			entire.readLock().unlock();
		}
	}
	
}