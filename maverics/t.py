import requests
import json

url = "http://localhost:8080/user/register"

payload = json.dumps({
  "firstName": "Anushka",
  "lastName": "J",
  "username": "A",
  "email": "anushka@sahaj.ai",
  "phoneNumber": "9876543210"
})
headers = {
  'Content-Type': 'application/json'
}

for i in range(10000000):
   response = requests.request("POST", url, headers=headers, data=payload)

print(response.text)

