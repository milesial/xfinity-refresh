import socket
from argparse import ArgumentParser

from elevate import elevate
from spoofmac import set_interface_mac


def client():
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    parser.add_argument('-p', '--port', metavar='PORT', type=int, default=8888, dest='port')
    parser.add_argument('--host', metavar='PORT', type=str, dest='host')
    args = parser.parse_args()

    elevate(graphical=False)
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((args.host, args.port))
        while True:
            received = sock.recv(64).decode('ascii')
            print('⭘ Received pass for MAC', received)
            set_interface_mac(args.iface, received)


if __name__ == '__main__':
    client()
