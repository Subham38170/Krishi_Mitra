

from google import genai


api_key='AIzaSyB7c1JRXN0f5c6KlJSeo7hb7oYvPMX1GYs'



client = genai.Client(api_key=api_key)

response = client.models.generate_content(
    model="gemini-2.5-flash", contents="Hello"
)
print(response.text)