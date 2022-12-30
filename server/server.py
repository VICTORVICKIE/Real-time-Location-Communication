import asyncio
import logging
import os
from pathlib import Path

# firewall-cmd --zone=public --permanent --remove-port=80/tcp
# firewall-cmd --reload


class LocationShareServer:
    def __init__(self, host: str, port: int):
        self.__host: str = host
        self.__port: int = port
        self.__logger: logging.Logger = self.initialize_logger()
        self._clients: dict = {}

    @property
    def host(self) -> str:
        return self.__host

    @property
    def port(self) -> int:
        return self.__port

    @property
    def logger(self) -> logging.Logger:
        return self.__logger

    def initialize_logger(self) -> logging.Logger:
        path: Path = Path(os.path.join(os.getcwd(), "logs"))
        path.mkdir(parents=True, exist_ok=True)

        logger = logging.getLogger("Location Share Server")
        logger.setLevel(logging.DEBUG)

        file_handler = logging.FileHandler("./logs/location_share_server.log")

        file_handler.setLevel(logging.DEBUG)

        formatter = logging.Formatter("[%(asctime)s] - %(levelname)s - %(message)s",
                                      "%Y-%m-%d %H:%M:%S")

        file_handler.setFormatter(formatter)
        logger.addHandler(file_handler)

        return logger

    async def start(self):
        try:
            server = await asyncio.start_server(self.handle_client, self.host, self.port)
            async with server:
                self.logger.info("Launching server")
                await server.serve_forever()
        except Exception as e:
            self.logger.error(e)
        except KeyboardInterrupt:
            self.logger.warning("Keyboard Interrupt")

    async def handle_client(self, reader: asyncio.StreamReader, writer: asyncio.StreamWriter):
        address, port = writer.get_extra_info("peername")
        identifier: str = f"{address}:{port}"
        self._clients[identifier] = (reader, writer)

        self.logger.info(f"{identifier} -- connected")
        self.logger.info(f"All clients -- {list(self._clients.keys())}")
        await self.recieve_location(reader, identifier)

    async def recieve_location(self, reader: asyncio.StreamReader, identifier: str):
        try:
            while True:
                location_byte: bytes = await reader.read(1024)
                location: str = location_byte.decode().strip()
                if location:
                    await self.broadcast_location(identifier, location)
                else:
                    break
        except Exception as e:
            self.logger.error(e)
        finally:
            self.logger.info(f"{identifier} -- disconnected")
            del self._clients[identifier]

    async def broadcast_location(self, sender: str, location: str):
        for address, (_, writer) in self._clients.items():
            if sender != address:
                try:
                    data: str = f"{sender} -- {location}"
                    self.logger.info(data)
                    writer.write(data.encode())
                    await writer.drain()
                except Exception as e:
                    self.logger.error(e)


if __name__ == "__main__":
    location_share_server = LocationShareServer("", 5555)
    loop = asyncio.get_event_loop()
    loop.run_until_complete(location_share_server.start())
