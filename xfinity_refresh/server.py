import socketserver
import subprocess
import time
from argparse import ArgumentParser
from multiprocessing import Process, Queue
from queue import Empty

from elevate import elevate
from halo import Halo

from xfinity_refresh import change_activate


# get a pass and put MAC in queue every 50 mins
def background_loop(interface, queue):
    while True:
        mac = change_activate(interface)
        # disconnect interface so another one can connect
        subprocess.run(['sudo', 'ifconfig', interface, 'down'], stdout=PIPE, stderr=PIPE)
        queue.put({'mac': mac, 'time': time.time()})
        with Halo(spinner='clock') as s:
            for i in reversed(range(6 * 50)):
                s.text = 'Waiting {}m {}s until next pass...'.format(int(i / 60), i % 60)
                time.sleep(10)


def get_tcp_handler(queue):
    class TCPHandler(socketserver.BaseRequestHandler):
        def handle(self):
            print('⭘ Client {} connected!'.format(self.client_address[0]))
            last_pass = None
            try:
                while True:
                    last_pass = queue.get_nowait()
            except Empty:
                pass

            if last_pass is not None:
                self.request.sendall(last_pass['mac'].encode('ascii'))
                print('⭘ Sent {} to {}'.format(last_pass['mac'], self.client_address[0]))

            while True:
                last_pass = queue.get(block=True)
                self.request.sendall(last_pass['mac'].encode('ascii'))
                print('⭘ Sent {} to {}'.format(last_pass['mac'], self.client_address[0]))

    return TCPHandler


def server():
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    parser.add_argument('-p', '--port', metavar='PORT', type=int, default=8888, dest='port')
    args = parser.parse_args()

    elevate(graphical=False)
    mac_queue = Queue()
    Process(target=background_loop, args=(args.iface, mac_queue)).start()

    print('⭘ Serving TCP server on port', args.port)
    handler = get_tcp_handler(mac_queue)
    with socketserver.TCPServer(("0.0.0.0", args.port), handler) as server:
        server.serve_forever()


if __name__ == '__main__':
    server()
