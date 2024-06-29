
import 'dart:nativewrappers/_internal/vm/lib/typed_data_patch.dart';

import 'package:chat_gpt_sdk/chat_gpt_sdk.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class ChatGPTService {
  final String apiKey = dotenv.env["OPENAI_API_KEY"].toString();
  OpenAI? openAI;

  void init() {
    openAI = OpenAI.instance.build(
      token: apiKey,
      baseOption: HttpSetup(receiveTimeout: const Duration(seconds: 30)),
      enableLog: false
    );
  }

  Future<String> textPrompt(String prompt) async {
    final request = ChatCompleteText(messages: [
      Map.of({"role": "user", "content": prompt})
    ], maxToken: 200, model: Gpt4VisionPreviewChatModel());

    final response = await openAI?.onChatCompletion(request: request);

    return response?.choices[0].message?.content ?? "";
  }
  Future<ChatCTResponse?> imagePrompt(String prompt, List<ByteData> images) async {
    var messages = [
      {
        "role": "user",
        "content": [
          {"type": "text", "text": prompt},
        ]
      }
    ];
    for (var image in images) {
      (messages.first['content'] as List).add(Map.of({"type": "image_url", "image_url": {"url": "data:image/jpeg;base64,{$image}"}}));
    }
    final request = ChatCompleteText(
      messages: messages,
      maxToken: 200,
      model: Gpt4VisionPreviewChatModel(),
    );

    return await openAI?.onChatCompletion(request: request);
  }
}