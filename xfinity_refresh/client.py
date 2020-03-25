import socket
import sys
import time
from argparse import ArgumentParser
from select import select

from elevate import elevate
from halo import Halo
from spoofmac import set_interface_mac


def client():
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    parser.add_argument('-p', '--port', metavar='PORT', type=int, default=8888, dest='port')
    parser.add_argument('--host', metavar='PORT', type=str, dest='host')
    args = parser.parse_args()

    elevate(graphical=False)

    while True:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.connect((args.host, args.port))
            print('⭘ Connected to {}:{}'.format(args.host, args.port))
            mac = sock.recv(17).decode('ascii')
            time_left = int(sock.recv(10).decode('ascii'))
            print('⭘ Received pass for MAC', mac)

            set_interface_mac(args.iface, mac)

        with Halo(spinner='clock') as s:
            for i in reversed(range(time_left)):
                s.text = 'Waiting {}m {}s until next pass... [q]uit | [r]efresh'.format(int(i / 60), i % 60)
                rlist, _, _ = select([sys.stdin], [], [], 0)
                if rlist:
                    key = sys.stdin.readline()[0]
                    if key == 'q':
                        return
                    elif key == 'r':
                        break
                    else:
                        s.text = 'Command not recognized: {}'.format(key)

                time.sleep(1)


if __name__ == '__main__':
    client()
