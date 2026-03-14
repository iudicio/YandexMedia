import os

def merge_files(root_dir, output_file, encoding="utf-8"):
    with open(output_file, "w", encoding=encoding) as outfile:
        for root, dirs, files in os.walk(root_dir):
            for file in files:
                file_path = os.path.join(root, file)

                # Пропускаем сам выходной файл, чтобы избежать зацикливания
                if os.path.abspath(file_path) == os.path.abspath(output_file):
                    continue

                try:
                    with open(file_path, "r", encoding=encoding) as infile:
                        outfile.write(f"\n\n===== FILE: {file_path} =====\n\n")
                        outfile.write(infile.read())
                except Exception as e:
                    print(f"Не удалось прочитать {file_path}: {e}")

if __name__ == "__main__":
    source_directory = "./"  # Укажи путь к папке
    output_filename = "merged_output.txt"

    merge_files(source_directory, output_filename)
    print("Объединение завершено.")