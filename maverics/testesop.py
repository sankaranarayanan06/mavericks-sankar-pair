import requests
import json

urlBWallet = "http://localhost:8080/user/B/wallet"

payloadBWallet = json.dumps({
  "amount": 10000000
})
headers = {
  'Content-Type': 'application/json'
}

urlAInv  = "http://localhost:8080/user/A/inventory"

payloadAInv = json.dumps({
  "quantity": 10000000,
  "type": "NON_PERFORMANCE"
})

response1 = requests.request("POST", urlBWallet, headers=headers, data=payloadBWallet)
response2 = requests.request("POST", urlAInv, headers=headers, data=payloadAInv)
print(response1.text, response2.text)


