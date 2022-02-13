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

package com.kdgregory.kdgcommons.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;


/**
 *  A decorator that provides <code>InputStream</code> operations backed by a
 *  <code>java.nio.Channel</code>. This allows programs to use the flexibility
 *  of the channel API (such as random seeks on <code>FileChannel</code>)
 *  while still using the <code>java.io</code> class hierarchy (in particular,
 *  <code>InputStreamReader</code>).
 *  <p>
 *  This class does not attempt to replicate channel functionality. The program
 *  should maintain an independent reference to the channel to support operations
 *  such as <code>position()</code>. See method docs for cases where stream
 *  functionality overlaps channel functionality.
 *  <p>
 *  Single instances of this class are not safe for concurrent use by multiple
 *  threads. Multiple instances that share a channel are thread-safe if the
 *  channel is thread-safe.
 */
public class ChannelInputStream
extends InputStream
{
    private ReadableByteChannel channel;

    // for single-byte reads we'll reuse the buffer rather than recreate
    // (premature optimization, maybe, but why allocate objects we don't need?)
    private byte[] singleByte = new byte[1];
    private ByteBuffer singleByteBuf = ByteBuffer.wrap(singleByte);


    public ChannelInputStream(ReadableByteChannel channel)
    {
        this.channel = channel;
    }

//----------------------------------------------------------------------------
//  InputStream
//----------------------------------------------------------------------------

    /**
     *  Always returns <code>false</code>. If the underlying channel supports
     *  marks and positioning, you should perform those operations there.
     */
    @Override
    public boolean markSupported()
    {
        return false;
    }


    /**
     *  Reads a single byte from the channel. Returns the byte's value, or -1 to
     *  indicate either end-of-file or no bytes available on channel.
     *  <p>
     *  Unlike the <code>InputStream</code> implementations in <code>java.io</code>,
     *  end-of-file is "soft": some channels will increase their size. As a result,
     *  you can call this method after receiving end-of-file notification and get
     *  valid data.
     */
    @Override
    public int read()
    throws IOException
    {
        singleByteBuf.clear();
        int flag = channel.read(singleByteBuf);
        if (flag <= 0)
            return -1;
        return singleByte[0] & 0xFF;
    }


    /**
     *  Attempts to fill the passed array from the channel, returning the number of
     *  bytes read (which may be 0), or -1 to indicate end-of-file.
     *  <p>
     *  Unlike the <code>InputStream</code> implementations in <code>java.io</code>,
     *  end-of-file is "soft": some channels will increase their size. As a result,
     *  you can call this method after receiving end-of-file notification and get
     *  valid data.
     */
    @Override
    public int read(byte[] b) throws IOException
    {
        return read(b, 0, b.length);
    }


    /**
     *  Attempts to fill a section of the passed array from the channel, returning
     *  the number of bytes read (which may be 0), or -1 to indicate end-of-file.
     *  <p>
     *  Unlike the <code>InputStream</code> implementations in <code>java.io</code>,
     *  end-of-file is "soft": some channels will increase their size. As a result,
     *  you can call this method after receiving end-of-file notification and get
     *  valid data.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        ByteBuffer buf = ByteBuffer.wrap(b, off, len);
        return channel.read(buf);
    }


    /**
     *  Does nothing; closing the channel does not close the underlying stream.
     *  <p>
     *  Warning: this behavior differs from version 1.x, where this method did
     *  close the underlying stream. 
     */
    @Override
    public void close() throws IOException
    {
        // no-op
    }


    /**
     *  This method always returns 0; there is no way to discover the number of
     *  available bytes from a generic channel.
     */
    @Override
    public int available() throws IOException
    {
        return 0;
    }


    /**
     *  Attempts to skip the specified number of bytes from the channel.
     *  <p>
     *  Per <code>InputStream</code>, this is a best-effort method. You should either
     *  use {@link IOUtil#skipFully} or position the channel explicitly.
     */
    @Override
    public long skip(long n) throws IOException
    {
        return super.skip(n);
    }
}
