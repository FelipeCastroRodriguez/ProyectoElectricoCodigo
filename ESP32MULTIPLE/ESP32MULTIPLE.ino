/*************
 *  LIBRERIAS
 *************/
 
// Para la creacion del dipositivo BLE
// y el advertising
#include "BLEDevice.h"
#include "BLEServer.h"
#include "BLEUtils.h"
// Para que el ESP32 entre en deep sleep
#include "esp_sleep.h" 

/**********************
 *  VARIABLES GLOBALES
 **********************/
 
// Tiempo del deep sleep en x segundos
#define GPIO_DEEP_SLEEP_DURATION 1.8

BLEAdvertising *pAdvertising;

/********************************
 *  FUNCION PARA CREAR EL BEACON
 ********************************/

void setBeacon1() {
  // Tamano en bytes de la
  // carga util de advertising
  char beacon_data[22];
  // UUID de tramas Eddystone
  uint16_t beconUUID = 0xFEAA;

  BLEAdvertisementData oAdvertisementData = BLEAdvertisementData();

  // Banderas de trama Eddystone
  // 0x06 por definicion
  oAdvertisementData.setFlags(0x06); // GENERAL_DISC_MODE 0x02 | BR_EDR_NOT_SUPPORTED 0x04
  // UUID de tramas Eddystone
  oAdvertisementData.setCompleteServices(BLEUUID(beconUUID));

  // Datos de trama Eddystone-UID
  beacon_data[0] = 0x00;  // Tipo de trama Eddystone (Eddystone-UID)
  beacon_data[1] = 0xDA;  // Potencia de transmision a 0m 
  beacon_data[2] = 0x9A;  // Namespace[0]
  beacon_data[3] = 0x8E;  // Namespace[1]
  beacon_data[4] = 0xA7;  // Namespace[2]
  beacon_data[5] = 0x97;  // Namespace[3]
  beacon_data[6] = 0x4C;  // Namespace[4]
  beacon_data[7] = 0xC1;  // Namespace[5]
  beacon_data[8] = 0xEF;  // Namespace[6]
  beacon_data[9] = 0xA9;  // Namespace[7]
  beacon_data[10] = 0x75;  // Namespace[8]
  beacon_data[11] = 0x6B;  // Namespace[9]
  beacon_data[12] = 0x11;  // Instance[0]
  beacon_data[13] = 0x11;  // Instance[1]
  beacon_data[14] = 0x11;  // Instance[2]
  beacon_data[15] = 0x11;  // Instance[3]
  beacon_data[16] = 0x11;  // Instance[4]
  beacon_data[17] = 0x11;  // Instance[5]

  // Se establece la trama de advertising
  oAdvertisementData.setServiceData(BLEUUID(beconUUID), std::string(beacon_data, 18));
  // Respuesta en caso de escaneo
  pAdvertising->setScanResponseData(oAdvertisementData);

}
/************
 *  Beacon 2
 ************/
void setBeacon2() {
  char beacon_data[22];
  
  uint16_t beconUUID = 0xFEAA;
  
  BLEAdvertisementData oAdvertisementData = BLEAdvertisementData();

  oAdvertisementData.setFlags(0x06); // GENERAL_DISC_MODE 0x02 | BR_EDR_NOT_SUPPORTED 0x04
  oAdvertisementData.setCompleteServices(BLEUUID(beconUUID));

  beacon_data[0] = 0x00;  // Tipo de trama Eddystone (Eddystone-UID)
  beacon_data[1] = 0xD3;  // Potencia de transmision a 0m 
  beacon_data[2] = 0x9A;  // Namespace[0]
  beacon_data[3] = 0x8E;  // Namespace[1]
  beacon_data[4] = 0xA7;  // Namespace[2]
  beacon_data[5] = 0x97;  // Namespace[3]
  beacon_data[6] = 0x4C;  // Namespace[4]
  beacon_data[7] = 0xC1;  // Namespace[5]
  beacon_data[8] = 0xEF;  // Namespace[6]
  beacon_data[9] = 0xA9;  // Namespace[7]
  beacon_data[10] = 0x75;  // Namespace[8]
  beacon_data[11] = 0x6B;  // Namespace[9]
  beacon_data[12] = 0x22;  // Instance[0]
  beacon_data[13] = 0x22;  // Instance[1]
  beacon_data[14] = 0x22;  // Instance[2]
  beacon_data[15] = 0x22;  // Instance[3]
  beacon_data[16] = 0x22;  // Instance[4]
  beacon_data[17] = 0x22;  // Instance[5]

  oAdvertisementData.setServiceData(BLEUUID(beconUUID), std::string(beacon_data, 18));

  pAdvertising->setScanResponseData(oAdvertisementData);
}

/*************************
 *  CODIGO DE PREPARACION
 *************************/

void setup() {
  /*
   *  En teoria este codigo solo deberia ejecutarse
   *  una vez, pero como el ESP32 se reinicia despues de salir
   *  del modo deep sleep entonces este codigo se repite de
   *  forma indefinida
   */

  // Crea el dispositivo BLE
  BLEDevice::init("");

  // Se obtiene los datos que van a ser
  // advertised
  pAdvertising = BLEDevice::getAdvertising();

  // Potencia de transmision
  // se utiliza el menor valor posible (-12 dbm)
  // para el mayor ahorro de energia
  esp_err_t errRc=::esp_ble_tx_power_set(ESP_BLE_PWR_TYPE_ADV, ESP_PWR_LVL_N12 );

  // Se crea el primer beacon
  setBeacon1();
  /* Proceso de advertising
   * durante 0.1 segundos y
   * luego se detiene
   */
  pAdvertising->start();
  delay(100);
  pAdvertising->stop();

  // Mismo proceso para 
  // el segundo beacon
  setBeacon2();
  pAdvertising->start();
  delay(100);
  pAdvertising->stop();

  /* 
   * ESP32 entra en modo de deep sleep
   * esta funcion utiliza microsegundos 
   * por ello se utiliza el factor de 
   * 1000000LL
   */
  esp_deep_sleep(1000000LL * GPIO_DEEP_SLEEP_DURATION);
}

/*****************
*   CODIGO LOOP
******************/

void loop() {
  /*
   *  El loop se repite de forma indefinida y
   *  en teoria es la parte principal
   *  del codigo en Arduino. El ESP32 nunca
   *  llega a esta parte porque se reinicia despues
   *  de salir del modo deep sleep y entonces ejecuta
   *  todo el programa desde el principio
   */
}

/********
 *  MISC
 ********/
 /*
 * Mediciones de potencia @1m
 * N12 -> -86 dBm
 * N9  -> -82 dBm
 * N6  -> -74 dbm
 * N3  -> -67 dBm
 * N0  -> -63 dBm
 */
