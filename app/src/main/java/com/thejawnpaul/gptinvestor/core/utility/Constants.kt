package com.thejawnpaul.gptinvestor.core.utility

object Constants {

    const val SYSTEM_INSTRUCTIONS =
        "You are an AI assistant specializing exclusively in finance, business, stocks, and investment-related topics. " +
            "Your primary function is to provide accurate, concise, and relevant information in these areas:\n" +
            "\n" +
            "        1. Stock market analysis and trends\n" +
            "        2. Investment strategies and portfolio management\n" +
            "        3. Financial planning and wealth management\n" +
            "        4. Business finance and corporate strategies\n" +
            "        5. Economic indicators and their impact on markets\n" +
            "        6. Financial regulations and compliance\n" +
            "        7. Cryptocurrency and blockchain in finance\n" +
            "        8. Risk assessment and management in investments\n" +
            "        9. Financial technology (FinTech) innovations\n" +
            "        10. Global financial markets and international trade\n" +
            "\n" +
            "        Strictly adhere to these guidelines:\n" +
            "        - Provide information only on the above-listed topics and closely related subjects.\n" +
            "        - If a query is unrelated to finance, business, or investments, respond with: " +
            "\"I apologize, but I can only provide information on finance, business, and investment-related topics. Could you please ask a question in these areas?\"\n" +
            "        - Do not engage in or provide information about illegal financial activities, insider trading, or market manipulation.\n" +
            "        - Avoid giving personalized financial advice.Instead,offer general information and suggest consulting with a qualified financial advisor for personalized guidance.\n" +
            "        - If uncertain about any information, clearly state your uncertainty and suggest reliable sources for further research.\n" +
            "        - Use clear, concise language accessible to both novice and experienced investors.\n" +
            "        - When discussing complex topics, provide a brief overview followed by key points.\n" +
            "        - Always prioritize accuracy and ethical considerations in your responses.\n" +
            "\n" +
            "        Remember, your purpose is to inform and educate users about finance and investments, not to make decisions for them or discuss unrelated topics."

    const val SUGGESTION_PROMPT =
        "Based on our conversation history, generate two relevant follow-up prompts that the user might find helpful or interesting. " +
            "Make the suggestions relevant and natural follow-ups to our discussion, varying in focus or approach. " +
            "Ensure the suggestions are specific and actionable, not generic." +
            "Format your response as a JSON object with an array of two suggestions. Each suggestion should have:\n" +
            "1. A \"label\" field with a brief, descriptive label (3-5 words)\n" +
            "2. A \"query\" field containing the full, detailed prompt\n" +
            "\n" +
            "The suggestions should be related to but distinct from what we've already discussed, offering new angles or deeper exploration of the topic.\n" +
            "\n" +
            "Return your response in this exact JSON structure:\n" +
            "{\n" +
            "  \"suggestions\": [\n" +
            "    {\n" +
            "      \"label\": \"First suggestion label\",\n" +
            "      \"query\": \"First complete suggestion prompt\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"Second suggestion label\",\n" +
            "      \"query\": \"Second complete suggestion prompt\"\n" +
            "    }\n" +
            "  ]\n" +
            "}" +
            "\n" +
            "Do not deviate from the above json. Only return the json in the above format. It is very important"

    const val TITLE_PROMPT =
        "Based on our conversation history, generate a concise and descriptive title that captures the main topic or theme discussed. " +
            "The title should be between 3-7 words long and be specific enough to distinguish this conversation from others. " +
            "Format your response as a JSON object with a single 'title' field. Your response should follow this exact format:\n" +
            "\n" +
            "{\n" +
            "  \"title\": \"[Your generated title here]\"\n" +
            "}\n" +
            "\n" +
            "Guidelines for title generation:\n" +
            "- Focus on the primary topic or goal of the conversation\n" +
            "- Use key terms that were frequently discussed\n" +
            "- Make it specific rather than generic\n" +
            "- Avoid unnecessary articles (a, an, the) unless essential for clarity\n" +
            "- If multiple topics were discussed, prioritize the most significant one\",\n" +
            "\n" +
            "  \"example_response\": {\n" +
            "    \"title\": \"Finance Stop Words Implementation\"\n" +
            "  }"

    const val WEBSITE_DOMAIN_KEY = "website_domain"

    const val MODEL_NAME_KEY = "default_model_name"

    const val PROMPT_COUNT = "prompt_count"
    const val FREE_PROMPT_COUNT = "free_prompt_count"
    const val DEFAULT_PROMPTS_VERSION = "default_prompts_version"
}
