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

package net.sf.kdgcommons.lang;

/**
 *  An object whose role is producing other objects. This interface is used by
 *  {@link net.sf.kdgcommons.collections.DefaultMap} and {@link ObjectUtil#defaultValue}.
 *
 *  @deprecated
 *  This interface has been made obsolete by Java8. It will be removed in version 2.0.
 */
@Deprecated
public interface ObjectFactory<T>
{
    /**
     *  Returns an instance of the object. Specific implementations may choose
     *  to return a new instance or a cached instance.
     */
    public T newInstance();
}
