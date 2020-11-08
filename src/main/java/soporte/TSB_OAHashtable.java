package soporte;

import java.io.Serializable;
import java.util.*;

public class TSB_OAHashtable<K,V> implements Map<K,V>, Cloneable, Serializable
{
    //************************ Constantes (privadas o públicas).

    // el tamaño máximo que podrá tener el arreglo de soprte...
    private final static int MAX_SIZE = Integer.MAX_VALUE;


    //************************ Atributos privados (estructurales).

    // la tabla hash: el arreglo que contiene las entradas a almacenar...
    private Entry<K, V> [] array;

    // el tamaño inicial de la tabla (tamaño con el que fue creada)...
    private int initial_capacity;

    // la cantidad de objetos que contiene la tabla...
    private int count;

    // el factor de carga para calcular si hace falta un rehashing...
    private float load_factor;

    //************************ Atributos privados (para gestionar las vistas).

    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Collection<V> values = null;


    //************************ Atributos protegidos (control de iteración).

    // conteo de operaciones de cambio de tamaño (fail-fast iterator).
    protected transient int modCount;

    //************************ Constructores.

    /**
     * Crea una tabla vacía, con la capacidad inicial igual a 11 y con factor
     * de carga igual a 0.5f.
     */
    public TSB_OAHashtable()
    {
        this(11, 0.5f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con factor
     * de carga igual a 0.5f.
     * @param initial_capacity la capacidad inicial de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity)
    {
        this(initial_capacity, 0.5f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor
     * de carga indicado. Si la capacidad inicial indicada por initial_capacity
     * es menor o igual a 0, la tabla será creada de tamaño 11. Si el valor de
     * initial_capacity no es un número primo, se establecerá como capacidad
     * inicial el siguiente número primo, para garantizar que la exploración
     * cuadrática funcione. Si el factor de carga indicado es negativo, cero
     * o mayor a 0.5f se ajustará a 0.5f para garantizar el correcto funcionamiento
     * del direccionamiento cuadrático.
     * @param initial_capacity la capacidad inicial de la tabla.
     * @param load_factor el factor de carga de la tabla.
     */

    public TSB_OAHashtable(int initial_capacity, float load_factor)
    {
        if(load_factor <= 0 || load_factor > 0.5f) { load_factor = 0.5f; }
        if(initial_capacity <= 0) { initial_capacity = 11; }
        else
        {
            if(initial_capacity > TSB_OAHashtable.MAX_SIZE)
            {
                initial_capacity = TSB_OAHashtable.MAX_SIZE;
            }
        }

        this.initial_capacity = siguientePrimo(initial_capacity);
        this.array = new Entry[this.initial_capacity];

        this.load_factor = load_factor;
        this.count = 0;
        this.modCount = 0;
    }

    /**
     * Crea una tabla a partir del contenido del Map especificado.
     * @param t el Map a partir del cual se creará la tabla.
     */
    public TSB_OAHashtable(Map<? extends K,? extends V> t)
    {
        this(11, 0.5f);
        this.putAll(t);
    }


    //************************ Implementación de métodos especificados por Map.

    /**
     * Retorna la cantidad de elementos contenidos en la tabla.
     * @return la cantidad de elementos de la tabla.
     */
    @Override
    public int size()
    {
        return this.count;
    }

    /**
     * Determina si la tabla está vacía (no contiene ningún elemento).
     * @return true si la tabla está vacía.
     */
    @Override
    public boolean isEmpty()
    {
        return (this.count == 0);
    }

    /**
     * Determina si la clave key está en la tabla.
     * @param key la clave a verificar.
     * @return true si la clave está en la tabla.
     * @throws NullPointerException si la clave es null.
     */
    @Override
    public boolean containsKey(Object key)
    {
        return (this.get((K)key) != null);
    }

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a contains().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    @Override
    public boolean containsValue(Object value)
    {
        return this.contains(value);
    }

    /**
     * Retorna el objeto al cual está asociada la clave key en la tabla, o null
     * si la tabla no contiene ningún objeto asociado a esa clave.
     * @param key la clave que será buscada en la tabla.
     * @return el objeto asociado a la clave especificada (si existe la clave) o
     *         null (si no existe la clave en esta tabla).
     * @throws NullPointerException si key es null.
     * @throws ClassCastException si la clase de key no es compatible con la
     *         tabla.
     */
    @Override
    public V get(Object key)
    {
        if(key == null) throw new NullPointerException("get(): parámetro null");

        Entry<K, V> x = this.getEntry((K) key);
        return (x != null)? x.getValue() : null;
    }

    /**
     * Asocia el valor (value) especificado, con la clave (key) especificada en
     * esta tabla. Si la tabla contenía previamente un valor asociado para la
     * clave, entonces el valor anterior es reemplazado por el nuevo (y en este
     * caso el tamaño de la tabla no cambia).
     * @param key la clave del objeto que se quiere agregar a la tabla.
     * @param value el objeto que se quiere agregar a la tabla.
     * @return el objeto anteriormente asociado a la clave si la clave ya
     *         estaba asociada con alguno, o null si la clave no estaba antes
     *         asociada a ningún objeto.
     * @throws NullPointerException si key es null o value es null.
     */
    @Override
    public V put(K key, V value)
    {
        if(key == null || value == null) throw new NullPointerException("put(): parámetro null");
        V old = null;

        if(this.needsRehash()) this.rehash();

        int firstTombstone = -1;
        int index = this.h(key);
        int cuadCount = 0;
        while (true)
        {
            Entry<K, V> entry = array[index];
            if (entry == null)
            {
                Entry<K, V> newEntry = new Entry<>(key, value);
                if (firstTombstone == -1)
                    array[index] = newEntry;
                else
                    array[firstTombstone] = newEntry;
                this.count++;
                break;
            }
            if (entry.isTombstone())
            {
                if (firstTombstone == -1) firstTombstone = index;
            }
            else if(entry.getKey().equals(key))
            {
                old = entry.setValue(value);
                break;
            }

            cuadCount++;
            index = nextIndex(index, cuadCount);
        }

        this.modCount++;
        return old;
    }

    /**
     * Elimina de la tabla la clave key (y su correspondiente valor asociado).
     * El método no hace nada si la clave no está en la tabla.
     * @param key la clave a eliminar.
     * @return El objeto al cual la clave estaba asociada, o null si la clave no
     *         estaba en la tabla.
     * @throws NullPointerException - si la key es null.
     */
    @Override
    public V remove(Object key)
    {
        if(key == null) throw new NullPointerException("remove(): parámetro null");

        Entry<K, V> entry = this.getEntry((K) key);
        V old = null;
        if(entry != null)
        {
            old = entry.getValue();
            entry.markAsTombstone();
            this.count--;
            this.modCount++;
        }
        return old;
    }

    /**
     * Copia en esta tabla, todos los objetos contenidos en el map especificado.
     * Los nuevos objetos reemplazarán a los que ya existan en la tabla
     * asociados a las mismas claves (si se repitiese alguna).
     * @param m el map cuyos objetos serán copiados en esta tabla.
     * @throws NullPointerException si m es null.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        if(m == null) throw new NullPointerException("putAll(): parámetro null");

        for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
        {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Elimina todo el contenido de la tabla, de forma de dejarla vacía. En esta
     * implementación además, el arreglo de soporte vuelve a tener el tamaño que
     * inicialmente tuvo al ser creado el objeto.
     */
    @Override
    public void clear()
    {
        this.array = new Entry[initial_capacity];
        this.count = 0;
        this.modCount++;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todas las claves (key)
     * contenidas en la tabla. El conjunto está respaldado por la tabla, por lo
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando
     * sobre el conjunto vista, el resultado de la iteración será indefinido
     * (salvo que la modificación sea realizada por la operación remove() propia
     * del iterador, o por la operación setValue() realizada sobre una entrada
     * de la tabla que haya sido retornada por el iterador). El conjunto vista
     * provee métodos para eliminar elementos, y esos métodos a su vez
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Set.remove(), removeAll(), retainAll()
     * y clear()). El conjunto vista no soporta las operaciones add() y
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     * @return un conjunto (un Set) a modo de vista de todas las claves
     *         mapeadas en la tabla.
     */
    @Override
    public Set<K> keySet()
    {
        if(keySet == null)
            keySet = new KeySet();

        return keySet;
    }

    /**
     * Retorna una Collection (colección) a modo de vista de todos los valores
     * (values) contenidos en la tabla. La colección está respaldada por la
     * tabla, por lo que los cambios realizados en la tabla serán reflejados en
     * la colección, y viceversa. Si la tabla es modificada mientras un iterador
     * está actuando sobre la colección vista, el resultado de la iteración será
     * indefinido (salvo que la modificación sea realizada por la operación
     * remove() propia del iterador, o por la operación setValue() realizada
     * sobre una entrada de la tabla que haya sido retornada por el iterador).
     * La colección vista provee métodos para eliminar elementos, y esos métodos
     * a su vez eliminan el correspondiente par (key, value) de la tabla (a
     * través de las operaciones Iterator.remove(), Collection.remove(),
     * removeAll(), removeAll(), retainAll() y clear()). La colección vista no
     * soporta las operaciones add() y addAll() (si se las invoca, se lanzará
     * una UnsuportedOperationException).
     * @return una colección (un Collection) a modo de vista de todas los
     *         valores mapeados en la tabla.
     */
    @Override
    public Collection<V> values()
    {
        if(values==null)
            values = new ValueCollection();

        return values;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todos los pares (key, value)
     * contenidos en la tabla. El conjunto está respaldado por la tabla, por lo
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando
     * sobre el conjunto vista, el resultado de la iteración será indefinido
     * (salvo que la modificación sea realizada por la operación remove() propia
     * del iterador, o por la operación setValue() realizada sobre una entrada
     * de la tabla que haya sido retornada por el iterador). El conjunto vista
     * provee métodos para eliminar elementos, y esos métodos a su vez
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Set.remove(), removeAll(), retainAll()
     * and clear()). El conjunto vista no soporta las operaciones add() y
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     * @return un conjunto (un Set) a modo de vista de todos los objetos
     *         mapeados en la tabla.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        if(entrySet == null)
            entrySet = new EntrySet();

        return entrySet;
    }


    //************************ Redefinición de métodos heredados desde Object.

    /**
     * Retorna una copia superficial de la tabla. Los elementos contenidos en la tabla
     * son los mismos objetos que posee la nueva tabla.
     * @return una copia superficial de la tabla.
     * @throws java.lang.CloneNotSupportedException si la clase no implementa la
     *         interface Cloneable.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        TSB_OAHashtable<K, V> t = (TSB_OAHashtable<K, V>)super.clone();
        t.array = array.clone();
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    }

    /**
     * Determina si esta tabla es igual al objeto espeficicado.
     * @param obj el objeto a comparar con esta tabla.
     * @return true si los objetos son iguales.
     */
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Map)) { return false; }

        Map<K, V> t = (Map<K, V>) obj;
        if(t.size() != this.size()) { return false; }

        try
        {
            Iterator<Map.Entry<K,V>> i = this.entrySet().iterator();
            while(i.hasNext())
            {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if(t.get(key) == null) { return false; }
                else
                {
                    if(!value.equals(t.get(key))) { return false; }
                }
            }
        }

        catch (ClassCastException | NullPointerException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Retorna un hash code para la tabla completa.
     * @return un hash code para la tabla.
     */
    @Override
    public int hashCode()
    {
        if(this.isEmpty()) {return 0;}

        int hc = 0;
        for(Map.Entry<K, V> entry : this.entrySet())
        {
            hc += entry.hashCode();
        }

        return hc;
    }

    /**
     * Devuelve el contenido de la tabla en forma de String. Sólo por razones de
     * didáctica, se hace referencia explícita en esa cadena al contenido de
     * cada una de las listas de desborde o buckets de la tabla.
     * @return una cadena con el contenido completo de la tabla.
     */
    @Override
    public String toString()
    {
        StringBuilder cad = new StringBuilder("");

        for(Map.Entry<K, V> entry : entrySet())
        {
            cad.append("\nKey: ").append(entry.getKey()).append(" | Value: ").append(entry.getValue());
        }
        return cad.toString();
    }


    //************************ Métodos específicos de la clase.

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a containsValue().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    public boolean contains(Object value)
    {
        if(value == null) return false;

        Iterator <V> it = this.values.iterator();
        while(it.hasNext())
        {
            V element = it.next();
            if(value.equals(element)) return true;
        }
        return false;
    }

    /**
     * Incrementa el tamaño de la tabla y reorganiza su contenido. Se invoca
     * automaticamente cuando se detecta que la ocupación de la tabla supera
     * el valor critico dado por load_factor.
     */
    protected void rehash()
    {
        int old_length = this.array.length;

        // nuevo tamaño: asegura que siempre sea primo
        int new_length = siguientePrimo(old_length * 2);

        // no permitir que la tabla tenga un tamaño mayor al límite máximo...
        // ... para evitar overflow y/o desborde de índices...
        if(new_length > TSB_OAHashtable.MAX_SIZE)
        {
            new_length = TSB_OAHashtable.MAX_SIZE;
        }

        // crear el nuevo arreglo con capacidad new_length
        Entry<K, V> temp[] = new Entry[new_length];

        // notificación fail-fast iterator... la tabla cambió su estructura...
        this.modCount++;

        // recorrer el viejo arreglo y redistribuir los objetos que tenia...
        Iterator <Map.Entry<K, V>> it = this.entrySet().iterator();

        while(it.hasNext())
        {
            Map.Entry<K, V> x = it.next();

            // obtener el nuevo valor de dispersión del objeto para el nuevo arreglo...
            K key = x.getKey();
            int index = this.h(key, temp.length);

            // insertarlo en el nuevo arreglo
            int cuadCount = 0;
            while(temp[index] != null)
            {
                cuadCount++;
                index = nextIndex(index, cuadCount, new_length);
            }
            temp[index] = (Entry<K, V>) x;
        }

        // cambiar la referencia array para que apunte a temp
        this.array = temp;
    }


    //************************ Métodos privados.

    /*
     * Función hash. Toma una clave entera k y calcula y retorna un índice
     * válido para esa clave para entrar en la tabla.
     */
    private int h(int k)
    {
        return h(k, this.array.length);
    }

    /*
     * Función hash. Toma un objeto key que representa una clave y calcula y
     * retorna un índice válido para esa clave para entrar en la tabla.
     */
    private int h(K key)
    {
        return h(key.hashCode(), this.array.length);
    }

    /*
     * Función hash. Toma un objeto key que representa una clave y un tamaño de
     * tabla t, y calcula y retorna un índice válido para esa clave dedo ese
     * tamaño.
     */
    private int h(K key, int t)
    {
        return h(key.hashCode(), t);
    }

    /*
     * Función hash. Toma una clave entera k y un tamaño de tabla t, y calcula y
     * retorna un índice válido para esa clave dado ese tamaño.
     */
    private int h(int k, int t)
    {
        if(k < 0) k *= -1;
        return k % t;
    }

    /*
     * Método que determina si se precisa realizar rehash a la tabla según la ocupación de la misma.
     */
    private boolean needsRehash(){
        return (count + 1 >= array.length * load_factor);
    }

    /*
     * Método para obtener el siguiente índice según la exploración cuadrática,
     * utilizando como valor de longitud la longitud de la tabla hash.
     */
    private int nextIndex(int index, int i)
    {
        return nextIndex(index, i, array.length);
    }

    /*
     * Devuelve el índice siguiente según el método de exploración cuadrática.
     * Recibe como parámetro el índice actual y un entero que indica el número de
     * vuelta en la exploración.
     */
    private int nextIndex(int index, int i, int lenght){
        return (index + i*i) % lenght;
    }

//    private int nextIndex(int index, int i, int lenght){
//        int nextIndex = index + i*i;
//        if (nextIndex >= lenght) return nextIndex % lenght;
//        return nextIndex;
//    }
//    // Exploración secuencial
//    private int nextIndex(int index, int i, int lenght){
//        if (index + 1 == lenght) return 0;
//        return index+1;
//    }

    /*
     * Recibe como parámetro una llave y busca en la tabla la entry correspondiente a esa llave.
     * Si la encuentra devuelve el valor asociado a la llave.
     * Si no encuentra ninguna entry, devuelve null.
     */
    private Entry<K,V> getEntry(K key){
        int index = this.h(key);
        int count = 0;
        while (true){
            Entry<K, V> entry = array[index];
            if (entry == null) return null;
            if (entry.getKey().equals(key) && !entry.isTombstone()) return array[index];
            count++;
            index = nextIndex(index, count);
        }
    }

    /*
     * Método que dado un número, busca y devuelve el número primo siguiente al mismo.
     */
    private int siguientePrimo ( int n )
    {
        if ( n % 2 == 0) n++;
        for ( ; !esPrimo(n); n+=2 ) ;
        return n;
    }

    /*
     * Método que recibe un número por parámetro y devuelve true si éste es primo.
     * En caso contrario devuelve false.
     */
    private boolean esPrimo(int numero){
        int contador = 2;
        boolean primo=true;
        while ((primo) && (contador!=numero)){
            if (numero % contador == 0)
                primo = false;
            contador++;
        }
        return primo;
    }


    //************************ Clases Internas.

    /*
     * Clase interna que representa los pares de objetos que se almacenan en la
     * tabla hash: son instancias de esta clase las que realmente se guardan en
     * en cada una de las listas del arreglo table que se usa como soporte de
     * la tabla. Lanzará una IllegalArgumentException si alguno de los dos
     * parámetros es null.
     */
    private class Entry<K, V> implements Map.Entry<K, V>
    {
        private K key;
        private V value;
        private boolean tombstone;

        public Entry(K key, V value)
        {
            if(key == null || value == null)
            {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
            this.key = key;
            this.value = value;
            this.tombstone = false;
        }

        /*
         * Método que indica si la entry está marcada como tumba.
         */
        public boolean isTombstone(){ return tombstone; }

        /*
         * Método que al ser invocado marca a la entry como tumba.
         */
        public void markAsTombstone(){ tombstone = true; }

        @Override
        public K getKey()
        {
            return key;
        }

        @Override
        public V getValue()
        {
            return value;
        }

        @Override
        public V setValue(V value)
        {
            if(value == null)
            {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }

            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (this.getClass() != obj.getClass()) { return false; }

            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) { return false; }
            if (!Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        @Override
        public String toString()
        {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }

    /*
     * Clase interna que representa una vista de todas los Claves mapeadas en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class KeySet extends AbstractSet<K>
    {
        @Override
        public Iterator<K> iterator()
        {
            return new KeySetIterator();
        }

        @Override
        public int size()
        {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public boolean contains(Object o)
        {
            return TSB_OAHashtable.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o)
        {
            return (TSB_OAHashtable.this.remove(o) != null);
        }

        @Override
        public void clear()
        {
            TSB_OAHashtable.this.clear();
        }

        private class KeySetIterator implements Iterator<K>
        {
            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            // valor del índice del elemento actual en la iteración.
            private int current_index;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el
             * mecanismo fail-fast.
             */
            public KeySetIterator()
            {
                current_index = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya
             * sido retornado por next().
             */
            @Override
            public boolean hasNext()
            {
                Entry<K, V> [] array = TSB_OAHashtable.this.array;
                if(TSB_OAHashtable.this.isEmpty()) { return false; }
                if(current_index >= array.length) { return false; }
                int index = current_index;
                while (index +1 < array.length){
                    index ++;
                    Entry<K, V> entry = array[index];
                    if (entry != null && !entry.isTombstone()){
                        return true;
                    }
                }

                return false;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public K next()
            {
                // control: fail-fast iterator...
                if(TSB_OAHashtable.this.modCount != expected_modCount)
                {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if(!hasNext())
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                while (true){
                    current_index ++;
                    Entry<K, V> entry = array[current_index];
                    if (entry != null && !entry.isTombstone()){
                        next_ok = true;
                        return entry.getKey();
                    }
                }
            }

            /*
             * Remueve el elemento actual de la tabla (lo marca como tumba).
             * El elemento removido es el que fue retornado la última vez que
             * se invocó a next(). El método sólo puede ser invocado una vez
             * por cada invocación a next().
             */
            @Override
            public void remove()
            {
                if(!next_ok)
                {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                Entry<K, V> entry = TSB_OAHashtable.this.array[current_index];
                entry.markAsTombstone();

                next_ok = false;
                TSB_OAHashtable.this.count--;
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }
        }
    }

    /*
     * Clase interna que representa una vista de todos los PARES mapeados en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {

        @Override
        public Iterator<Map.Entry<K, V>> iterator()
        {
            return new EntrySetIterator();
        }

        /*
         * Verifica si esta vista (y por lo tanto la tabla) contiene al par
         * que entra como parámetro (que debe ser de la clase Entry).
         */
        @Override
        public boolean contains(Object o)
        {
            if(o == null) { return false; }
            if(!(o instanceof Entry)) { return false; }

            Map.Entry<K, V> entry = (Map.Entry<K,V>)o;
            K key = entry.getKey();

            Map.Entry<K,V> arrayEntry = TSB_OAHashtable.this.getEntry(key);

            return entry.equals(arrayEntry);
        }

        /*
         * Elimina de esta vista (y por lo tanto de la tabla) al par que entra
         * como parámetro (y que debe ser de tipo Entry).
         */
        @Override
        public boolean remove(Object o)
        {
            if(o == null) { throw new NullPointerException("remove(): parámetro null");}
            if(!(o instanceof Entry)) { return false; }

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();

            Entry<K, V> arrayEntry = TSB_OAHashtable.this.getEntry(key);

            if(entry.equals(arrayEntry))
            {
                arrayEntry.markAsTombstone();
                TSB_OAHashtable.this.count--;
                TSB_OAHashtable.this.modCount++;
                return true;
            }
            return false;
        }

        @Override
        public int size()
        {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public void clear()
        {
            TSB_OAHashtable.this.clear();
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>>
        {

            // valor del índice del elemento actual en la iteración.
            private int current_index;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el
             * mecanismo fail-fast.
             */
            public EntrySetIterator()
            {
                current_index = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya
             * sido retornado por next().
             */
            @Override
            public boolean hasNext()
            {

                Entry<K, V> [] array = TSB_OAHashtable.this.array;

                if(TSB_OAHashtable.this.isEmpty()) { return false; }
                if (current_index >= array.length )
                {
                    return false;
                }
                int index = current_index;
                while (index + 1< array.length)
                {
                    index++;
                    Entry<K, V> entry = array[index];
                    if (entry != null && !entry.isTombstone())
                    {
                        return true;
                    }
                }
                return false;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public Map.Entry<K, V> next()
            {
                // control: fail-fast iterator...
                if(TSB_OAHashtable.this.modCount != expected_modCount)
                {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if(!hasNext())
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                while (true){
                    current_index ++;
                    Entry<K, V> entry = array[current_index];
                    if (entry != null && !entry.isTombstone()){
                        next_ok = true;
                        return entry;
                    }
                }
            }

            /*
             * Remueve el elemento actual de la tabla (lo marca como tumba).
             * El elemento removido es el que fue retornado la última vez que
             * se invocó a next(). El método sólo puede ser invocado una vez
             * por cada invocación a next().
             */
            @Override
            public void remove()
            {
                if(!next_ok)
                {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                Entry<K, V> entry = TSB_OAHashtable.this.array[current_index];
                entry.markAsTombstone();

                next_ok = false;
                TSB_OAHashtable.this.count--;
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }
        }
    }

    /*
     * Clase interna que representa una vista de todos los VALORES mapeados en
     * la tabla: si la vista cambia, cambia también la tabla que le da respaldo,
     * y viceversa. La vista es stateless: no mantiene estado alguno (es decir,
     * no contiene datos ella misma, sino que accede y gestiona directamente los
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class ValueCollection extends AbstractCollection<V>
    {
        @Override
        public Iterator<V> iterator()
        {
            return new ValueCollectionIterator();
        }

        @Override
        public int size()
        {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public boolean contains(Object o)
        {
            return TSB_OAHashtable.this.containsValue(o);
        }

        @Override
        public void clear()
        {
            TSB_OAHashtable.this.clear();
        }

        private class ValueCollectionIterator implements Iterator<V>
        {
            // valor del índice del elemento actual en la iteración.
            private int current_index;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el
             * mecanismo fail-fast.
             */
            public ValueCollectionIterator()
            {
                current_index = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya
             * sido retornado por next().
             */
            @Override
            public boolean hasNext()
            {

              Entry<K, V>[] array = TSB_OAHashtable.this.array;

                if(TSB_OAHashtable.this.isEmpty()) { return false; }
                if (current_index >= array.length )
                {
                    return false;
                }
                int index = current_index;
                while (index + 1< array.length)
                {
                    index++;
                    Entry<K, V> entry = array[index];
                    if (entry != null && !entry.isTombstone())
                    {
                        return true;
                    }

                }
                return false;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public V next()
            {
                // control: fail-fast iterator...
                if(TSB_OAHashtable.this.modCount != expected_modCount)
                {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if(!hasNext())
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                while (true){
                    current_index ++;
                    Entry<K, V> entry = array[current_index];
                    if (entry != null && !entry.isTombstone()){
                        next_ok = true;
                        return entry.getValue();
                    }
                }

            }

            /*
             * Remueve el elemento actual de la tabla (lo marca como tumba).
             * El elemento removido es el que fue retornado la última vez que
             * se invocó a next(). El método sólo puede ser invocado una vez
             * por cada invocación a next().
             */
            @Override
            public void remove()
            {
                if(!next_ok)
                {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                Entry<K, V> entry = TSB_OAHashtable.this.array[current_index];
                entry.markAsTombstone();

                next_ok = false;
                TSB_OAHashtable.this.count--;
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;

            }
        }
    }
}

