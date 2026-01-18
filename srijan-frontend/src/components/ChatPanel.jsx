import { useRef, useEffect } from "react";

export default function ChatPanel({
  messages,
  inputValue,
  onInputChange,
  onGenerate,
  loading,
  downloadUrl,
}) {
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div className="w-96 flex flex-col bg-gray-900 border-r border-gray-800">

      <div className="p-4 border-b border-gray-800">
        <h2 className="text-white font-semibold">SRIJAN</h2>
        <p className="text-gray-400 text-xs">AI Spring Boot Generator</p>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.length === 0 && (
          <div className="text-gray-600 text-sm text-center mt-8">
            <p className="text-2xl mb-3">🚀</p>
            <p>Describe the Spring Boot project or feature you want to build.</p>
          </div>
        )}

        {messages.map((msg, index) => (
          <div
            key={index}
            className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}
          >
            <div
              className={`max-w-xs px-4 py-2 rounded-2xl text-sm leading-relaxed ${
                msg.role === "user"
                  ? "bg-blue-600 text-white rounded-br-sm"
                  : "bg-gray-800 text-gray-200 rounded-bl-sm"
              }`}
            >
              {msg.content}
            </div>
          </div>
        ))}

        {loading && (
          <div className="flex justify-start">
            <div className="bg-gray-800 text-gray-400 px-4 py-2 rounded-2xl rounded-bl-sm text-sm">
              Generating code...
            </div>
          </div>
        )}

        <div ref={bottomRef} />
      </div>

      {downloadUrl && (
        <div className="px-4 pb-2">
          <a
             href={`${import.meta.env.VITE_API_URL || "http://localhost:8080"}${downloadUrl}`}
            download="srijan-project.zip"
            className="block w-full text-center bg-green-700 hover:bg-green-600 text-white py-2 rounded-lg text-sm font-semibold transition-colors"
          >
            ⬇ Download ZIP
          </a>
        </div>
      )}

      <div className="p-4 border-t border-gray-800">
        <div className="flex gap-2">
          <textarea
            value={inputValue}
            onChange={(e) => onInputChange(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                onGenerate();
              }
            }}
            placeholder="Describe what you want to build..."
            rows={3}
            className="flex-1 bg-gray-800 text-white text-sm px-3 py-2 rounded-lg border border-gray-700 focus:outline-none focus:border-blue-500 resize-none"
          />
          <button
            onClick={onGenerate}
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white px-4 rounded-lg font-bold transition-colors"
          >
            →
          </button>
        </div>
        <p className="text-gray-600 text-xs mt-1">Enter to send · Shift+Enter for new line</p>
      </div>

    </div>
  );
}