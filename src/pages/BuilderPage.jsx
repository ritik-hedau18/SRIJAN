import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { generateCode } from "../api/srijanApi";
import { useAuth } from "../context/AuthContext";
import ChatPanel from "../components/ChatPanel";
import CodeEditor from "../components/CodeEditor";
import toast from "react-hot-toast";

export default function BuilderPage() {
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState("");
  const [loading, setLoading] = useState(false);
  const [files, setFiles] = useState([]);
  const [activeFile, setActiveFile] = useState(null);
  const [sessionId, setSessionId] = useState(null);
  const [downloadUrl, setDownloadUrl] = useState(null);

  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleGenerate = async () => {
    const prompt = inputValue.trim();
    if (!prompt || loading) return;

    // Add user message to chat
    setMessages((prev) => [...prev, { role: "user", content: prompt }]);
    setInputValue("");
    setLoading(true);

    try {
      const res = await generateCode({
        prompt,
        sessionId,    // null on first call, UUID on follow-ups
      });

      const { sessionId: newSessionId, downloadUrl, files } = res.data;

      // Save session for follow-up messages
      setSessionId(newSessionId);
      setDownloadUrl(downloadUrl);

      if (files && files.length > 0) {
        setFiles(files);
        setActiveFile(files[0].filePath);

        const fileNames = files.map((f) => f.filePath.split("/").pop()).join(", ");
        setMessages((prev) => [
          ...prev,
          {
            role: "ai",
            content: `Generated ${files.length} file(s): ${fileNames}. You can download the ZIP or ask me to modify anything.`,
          },
        ]);
      } else {
        setMessages((prev) => [
          ...prev,
          { role: "ai", content: "I couldn't parse any files from the response. Try rephrasing." },
        ]);
      }
    } catch (err) {
      toast.error("Generation failed. Please try again.");
      setMessages((prev) => [
        ...prev,
        { role: "ai", content: "Something went wrong. Please try again." },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="h-screen flex flex-col bg-gray-950">
      {/* Navbar */}
      <div className="flex items-center justify-between px-6 py-3 bg-gray-900 border-b border-gray-800">
        <span className="text-white font-bold text-lg">⚡ SRIJAN</span>
        <div className="flex items-center gap-4">
          <span className="text-gray-400 text-sm">Hello, {user?.name}</span>
          <button
            onClick={handleLogout}
            className="text-gray-400 hover:text-white text-sm transition-colors"
          >
            Logout
          </button>
        </div>
      </div>

      {/* Main Layout */}
      <div className="flex flex-1 overflow-hidden">
        <ChatPanel
          messages={messages}
          inputValue={inputValue}
          onInputChange={setInputValue}
          onGenerate={handleGenerate}
          loading={loading}
          downloadUrl={downloadUrl}
        />
        <CodeEditor
          files={files}
          activeFile={activeFile}
          onFileSelect={setActiveFile}
        />
      </div>
    </div>
  );
}