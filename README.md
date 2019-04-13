# TCPserver
This TCPserver first reads the input stream (is) current 10 byte.
Find the requested frame id
load the encoded frameid from disk to memory using read() API (which generally takes long time) --> Can be resolved by preloading

While sending first send the fid its sending and the filesize in 20 byte packet --> so client can know when a particular packet transmission is over
This helps to use the single TCP connection for all the requests.
Then send the encoded video.
