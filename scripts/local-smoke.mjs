import https from "node:https";

const baseUrl = "https://127.0.0.1:8443";
const agent = new https.Agent({ rejectUnauthorized: false });

function request(path, { method = "GET", headers = {}, body } = {}) {
  return new Promise((resolve, reject) => {
    const url = new URL(path, baseUrl);
    const req = https.request(
      url,
      {
        method,
        headers,
        agent,
      },
      (res) => {
        let data = "";
        res.setEncoding("utf8");
        res.on("data", (chunk) => {
          data += chunk;
        });
        res.on("end", () => {
          resolve({ statusCode: res.statusCode ?? 0, body: data });
        });
      },
    );

    req.on("error", reject);

    if (body) {
      req.write(body);
    }

    req.end();
  });
}

async function waitForHealth() {
  for (let attempt = 0; attempt < 40; attempt += 1) {
    try {
      const response = await request("/actuator/health");
      if (response.statusCode === 200 && response.body.includes('"status":"UP"')) {
        return response.body;
      }
    } catch {
      // ignore and retry
    }

    await new Promise((resolve) => setTimeout(resolve, 3000));
  }

  throw new Error("health check timed out");
}

async function main() {
  const health = await waitForHealth();

  const loginResponse = await request("/api/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      email: "admin@jaycodesx.dev",
      password: "StrongPass123!",
    }),
  });

  if (loginResponse.statusCode !== 200) {
    throw new Error(`admin login failed: ${loginResponse.statusCode} ${loginResponse.body}`);
  }

  const loginJson = JSON.parse(loginResponse.body);
  const token = loginJson.accessToken;

  const summaryResponse = await request("/api/metrics/admin/summary", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (summaryResponse.statusCode !== 200) {
    throw new Error(`admin summary failed: ${summaryResponse.statusCode} ${summaryResponse.body}`);
  }

  const quoteResponse = await request("/api/loan-quotes/public", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Session-Id": "smoke-session",
    },
    body: JSON.stringify({
      homePrice: 500000,
      downPayment: 100000,
      zipCode: "94107",
      loanProgram: "CONVENTIONAL",
      propertyUse: "PRIMARY_RESIDENCE",
      termYears: 30,
    }),
  });

  if (quoteResponse.statusCode < 200 || quoteResponse.statusCode >= 300) {
    throw new Error(`public quote failed: ${quoteResponse.statusCode} ${quoteResponse.body}`);
  }

  console.log("--- health ---");
  console.log(health);
  console.log("--- admin summary ---");
  console.log(summaryResponse.body);
  console.log("--- public quote ---");
  console.log(quoteResponse.body);
}

main().catch((error) => {
  console.error(error.message);
  process.exit(1);
});
