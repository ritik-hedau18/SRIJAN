import Editor from "@monaco-editor/react";

export default function CodeEditor({ files, activeFile, onFileSelect }) {
  if (!files || files.length === 0) {
    return (
      <div className="flex-1 bg-gray-950 flex items-center justify-center">
        <div className="text-center text-gray-600">
          <p className="text-5xl mb-4">{"</>"}</p>
          <p className="text-lg">Your generated code will appear here</p>
          <p className="text-sm mt-2">Type a prompt and hit Generate</p>
        </div>
      </div>
    );
  }

  const currentFile = files.find((f) => f.filePath === activeFile) || files[0];

  const getLanguage = (filePath) => {
    if (filePath.endsWith(".java")) return "java";
    if (filePath.endsWith(".xml")) return "xml";
    if (filePath.endsWith(".yml") || filePath.endsWith(".yaml")) return "yaml";
    if (filePath.endsWith(".properties")) return "ini";
    return "plaintext";
  };

  const getFileName = (filePath) => filePath.split("/").pop();

  return (
    <div className="flex-1 flex flex-col bg-gray-950 min-w-0">
      {/* File Tabs */}
      <div className="flex overflow-x-auto bg-gray-900 border-b border-gray-800">
        {files.map((file) => (
          <button
            key={file.filePath}
            onClick={() => onFileSelect(file.filePath)}
            className={`px-4 py-2 text-sm whitespace-nowrap border-r border-gray-800 transition-colors ${
              currentFile.filePath === file.filePath
                ? "bg-gray-950 text-white border-t-2 border-t-blue-500"
                : "text-gray-400 hover:text-white hover:bg-gray-800"
            }`}
          >
            {getFileName(file.filePath)}
          </button>
        ))}
      </div>

      {/* File Path Breadcrumb */}
      <div className="px-4 py-1 bg-gray-950 text-gray-600 text-xs border-b border-gray-800">
        {currentFile.filePath}
      </div>

      {/* Monaco Editor */}
      <div className="flex-1">
        <Editor
          height="100%"
          language={getLanguage(currentFile.filePath)}
          value={currentFile.content}
          theme="vs-dark"
          options={{
            readOnly: true,
            minimap: { enabled: false },
            fontSize: 14,
            scrollBeyondLastLine: false,
            wordWrap: "on",
          }}
        />
      </div>
    </div>
  );
}