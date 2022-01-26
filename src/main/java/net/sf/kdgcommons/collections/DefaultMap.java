// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.sf.kdgcommons.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.sf.kdgcommons.lang.ObjectFactory;


/**
 *  A <code>Map</code> decorator that will return a default value from {@link #get}
 *  if there is no mapping for the key. There are two ways to use this class: static
 *  and dynamic, and they're very different in how they interact with the underlying
 *  map.
 *  <p>
 *  The static usage returns a single constant value instance, and <em>does not</em>
 *  update the underlying map. This is useful to avoid null checks, for example when
 *  examining a list of parameters.
 *  <p>
 *  The dynamic usage invokes a factory to create new values, and stores those values
 *  in the underlying map. One example of this is a multi-map, where the factory will
 *  create new <code>List</code> or <code>Set</code> instances.
 *  <p>
 *  You can also construct an instance that reverses these usage patterns, and either
 *  returns a static value while updating the underlying map, or returns a dynamic
 *  value and leaves the delegate untouched.
 *  <p>
 *  Note that {@link #get} is the <em>only</em> method overridden by this class. The
 *  "contains" methods would be mostly useless if they considered default values, and
 *  the behavior of iterators would be difficult to define.
 *  
 *  @deprecated
 *  This functionality is now part of the base <code>Map</code> interface in Java8.
 *  Will be removed in version 2.0.
 */
@Deprecated
public class DefaultMap<K,V>
implements Map<K,V>, Serializable
{
    private static final long serialVersionUID = 1L;

    private Map<K,V> _delegate;
    private ObjectFactory<V> _valueFactory;
    private boolean _updateMap;

    /**
     *  Base constructor, allowing full configuration.
     *
     *  @deprecated
     *  This constructor has been made obsolete by Java8. It will be replaced in version 2.0.
     *
     *  @param  delegate    The underlying <code>Map</code>.
     *  @param  factory     A factory for new values.
     *  @param  update      Pass <code>true</code> to update the map when a default
     *                      value is returned, <code>false</code> to return the value
     *                      and leave the mapping missing.
     */
    @Deprecated
    public DefaultMap(Map<K,V> delegate, ObjectFactory<V> factory, boolean update)
    {
        _delegate = delegate;
        _valueFactory = factory;
        _updateMap = update;
    }


    /**
     *  Constructs an instance that returns a static value and does not update
     *  the underlying map.
     *
     *  @param  delegate    The underlying <code>Map</code>.
     *  @param  value       The value to return for missing mappings.
     */
    public DefaultMap(Map<K,V> delegate, V value)
    {
        this(delegate, new StaticValueFactory<V>(value), false);
    }


    /**
     *  Constructs an instance that returns a dynamic value and will update
     *  the underlying map.
     *
     *  @deprecated
     *  This constructor has been made obsolete by Java8. It will be replaced in version 2.0.
     *
     *  @param  delegate    The underlying <code>Map</code>.
     *  @param  factory     A factory for new values.
     */
    @Deprecated
    public DefaultMap(Map<K,V> delegate, ObjectFactory<V> factory)
    {
        this(delegate, factory, true);
    }


//----------------------------------------------------------------------------
//  The stuff that makes us unique
//----------------------------------------------------------------------------

    /**
     *  This interface defines a factory for default values.
     *
     *  @deprecated
     *  This interface has been made obsolete by Java8. It will be removed in version 2.0.
     */
    @Deprecated
    public interface ValueFactory<T>
    extends ObjectFactory<T>
    {
        public T newInstance();
    }


    /**
     *  An implementation of the {@link DefaultMap.ValueFactory} interface for
     *  static values. Used for testing, exposed in case you want an updating map
     *  with a static value (although why you'd want that, I don't know).
     *
     *  @deprecated
     *  This interface has been made obsolete by Java8. It will be removed in version 2.0.
     */
    @Deprecated
    public static class StaticValueFactory<T>
    implements ValueFactory<T>, Serializable
    {
        private static final long serialVersionUID = 1L;

        private T _value;

        public StaticValueFactory(T value)
        {
            _value = value;
        }

        public T newInstance()
        {
            return _value;
        }

        @Override
        public final boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            else if (obj instanceof StaticValueFactory)
            {
                StaticValueFactory<T> that = (StaticValueFactory<T>)obj;
                return this._value.equals(that._value);
            }
            return false;
        }


        @Override
        public final int hashCode()
        {
            return _value.hashCode();
        }
    }


    /**
     *  Retrieves an existing mapping, or returns the default value if no
     *  such mapping exists. Depending on configuration, may update the
     *  map with that default value.
     *  <p>
     *  This operation is not atomic. You will need external synchronization
     *  to make it so (or subclass and synchronize).
     */
    public V get(Object key)
    {
        if (_delegate.containsKey(key))
            return _delegate.get(key);

        V value = _valueFactory.newInstance();
        if (_updateMap)
            _delegate.put((K)key, value);

        return value;
    }


    /**
     *  Two instances are equal if their delegate maps and value factories are equal.
     *
     *  @deprecated
     *  This behavior will change in version 2.0: two maps will be equal if their
     *  delegates are equal and they have <em>the same</em> object factory. This
     *  method will also support comparing a <code>DefaultMap</code> to a normal
     *  <code>Map</code>.
     */
    @Override
    @Deprecated
    public final boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        else if (obj instanceof DefaultMap)
        {
            DefaultMap<?,?> that = (DefaultMap<?,?>)obj;
            return this._delegate.equals(that._delegate) && this._valueFactory.equals(that._valueFactory);
        }
        return false;
    }


    /**
     *  Returns the hashcode of the delegate map.
     */
    @Override
    public final int hashCode()
    {
        return _delegate.hashCode();
    }

//----------------------------------------------------------------------------
//  Everything else
//----------------------------------------------------------------------------

    /**
     *  {@inheritDoc}
     */
    public int size()
    {
        return _delegate.size();
    }

    /**
     *  {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return _delegate.isEmpty();
    }

    /**
     *  {@inheritDoc}
     */
    public boolean containsKey(Object key)
    {
        return _delegate.containsKey(key);
    }

    /**
     *  {@inheritDoc}
     */
    public boolean containsValue(Object value)
    {
        return _delegate.containsValue(value);
    }


    /**
     *  Stores a new value in the map, replacing any existing value. If there was
     *  an existing mapping for the key, will return the previous value. <em>Will
     *  not<em> return the default value.
     */
    public V put(K key, V value)
    {
        return _delegate.put(key, value);
    }

    /**
     *  Removes an existing mapping, if there was one, and returns the value of
     *  that mapping. <em>Will not<em> return the default value.
     */
    public V remove(Object key)
    {
        return _delegate.remove(key);
    }

    /**
     *  {@inheritDoc}
     */
    public void putAll(Map<? extends K,? extends V> m)
    {
        _delegate.putAll(m);
    }

    /**
     *  {@inheritDoc}
     */
    public void clear()
    {
        _delegate.clear();
    }

    /**
     *  {@inheritDoc}
     */
    public Set<K> keySet()
    {
        return _delegate.keySet();
    }

    /**
     *  {@inheritDoc}
     */
    public Collection<V> values()
    {
        return _delegate.values();
    }

    /**
     *  {@inheritDoc}
     */
    public Set<java.util.Map.Entry<K,V>> entrySet()
    {
        return _delegate.entrySet();
    }
}
