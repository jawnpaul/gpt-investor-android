import UIKit
import SwiftUI
import GPT_Investor

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @State var isActive: Bool = false
    var body: some View {
        ZStack {
            if self.isActive {
                ComposeView()
                    .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
            } else {
                ZStack(alignment: .center) {
                    Rectangle()
                        .foregroundColor(.splashBackground)
                        .frame(width: 172, height: 172)
                        .clipShape(Circle())
                    Image("SplashImage")
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: 150, height: 150, alignment: .center)
                }.frame(width: 200, height: 200)
            }
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                withAnimation {
                    self.isActive = true
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
