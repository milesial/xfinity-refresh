import socketserver
import subprocess
import threading
import time
from argparse import ArgumentParser
from multiprocessing import Process, Queue
from subprocess import PIPE

from elevate import elevate
from halo import Halo

from xfinity_refresh import change_activate

mutex = threading.Lock()

PASSES = []


# get a pass and put MAC in queue every 50 mins
def background_loop(interface, queue):
    while True:
        mac = change_activate(interface)
        # disconnect interface so another one can connect
        subprocess.run(['sudo', 'ifconfig', interface, 'down'], stdout=PIPE, stderr=PIPE)
        queue.put({'mac': mac, 'time': time.time() + 60 * 60})
        with Halo(spinner='clock') as s:
            for i in reversed(range(6 * 50)):
                s.text = 'Waiting {}m {}s until next pass...'.format(int(i / 6), (i * 10) % 60)
                time.sleep(10)


def background_once(interface, queue):
    mac = change_activate(interface)
    subprocess.run(['sudo', 'ifconfig', interface, 'down'], stdout=PIPE, stderr=PIPE)
    queue.put({'mac': mac, 'time': time.time() + 60 * 60})


class ThreadedTCPServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    pass


def server():
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    parser.add_argument('-p', '--port', metavar='PORT', type=int, default=8888, dest='port')
    args = parser.parse_args()

    elevate(graphical=False)

    class ThreadedTCPRequestHandler(socketserver.BaseRequestHandler):
        def handle(self):
            while True:
                mutex.acquire()
                if len(PASSES):
                    last_pass = PASSES.pop()
                    mutex.release()
                    self.request.sendall(last_pass['mac'].encode('ascii'))
                    time_left = int(last_pass['time'] - time.time())
                    self.request.sendall("{}".format(time_left).encode('ascii'))
                    print('⭘ Sent {} to {} ({}min remaining)'.format(last_pass['mac'],
                                                                     self.client_address[0],
                                                                     int(time_left / 60)))
                    return
                else:
                    mutex.release()
                    time.sleep(1)

    server = ThreadedTCPServer(('0.0.0.0', args.port), ThreadedTCPRequestHandler)
    print('⭘ Serving TCP server on port', args.port)
    server_thread = threading.Thread(target=server.serve_forever)
    server_thread.daemon = True
    server_thread.start()

    while True:
        with mutex:
            # if a pass will expire in less than 10 minutes, remove it
            PASSES[:] = list(filter(lambda p: p['time'] - time.time() > 10 * 60, PASSES))
            good_passes = list(filter(lambda p: p['time'] - time.time() > 40 * 60, PASSES))

        if len(good_passes) < 2:
            q = Queue()
            p = Process(target=background_once, args=(args.iface, q))
            p.start()
            p.join()
            with mutex:
                PASSES.append(q.get())

        time.sleep(5)


if __name__ == '__main__':
    server()
